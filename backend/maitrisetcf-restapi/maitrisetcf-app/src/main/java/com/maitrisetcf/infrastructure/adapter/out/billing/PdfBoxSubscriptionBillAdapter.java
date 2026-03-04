package com.maitrisetcf.infrastructure.adapter.out.billing;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.exceptions.TechnicalException;
import com.maitrisetcf.domain.models.appconfiguration.AppConfiguration;
import com.maitrisetcf.domain.models.subscription.UserSubscription;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.ports.out.FileStoragePort;
import com.maitrisetcf.domain.ports.out.SubscriptionBillPort;
import com.maitrisetcf.domain.ports.out.persistenceport.AppConfigurationPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * PDFBox adapter generating subscription bills as PDF files.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfBoxSubscriptionBillAdapter implements SubscriptionBillPort {
    private static final String BILLS_DIRECTORY = "bills";
    private static final PDFont FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter SHORT_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.LETTER.getHeight();
    private static final float PAGE_MARGIN = 28f;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (PAGE_MARGIN * 2);
    private static final float BODY_FONT_SIZE = 10.5f;
    private static final float SMALL_FONT_SIZE = 8.5f;
    private static final float HEADER_LOGO_WIDTH = 38f;
    private static final float HEADER_LOGO_HEIGHT = 38f;
    private static final String ENTERPRISE_NAME = "NAME";
    private static final String ENTERPRISE_ADDRESS = "ADDRESS";
    private static final String ENTERPRISE_PHONE_NUMBER = "PHONE_NUMBER";
    private static final String ENTERPRISE_EMAIL = "EMAIL";
    private static final String LOGO_RESOURCE_PATH = "billing/enterprise-logo.png";

    private final FileStoragePort fileStoragePort;
    private final AppConfigurationPersistencePort appConfigurationPersistencePort;

    @Override
    public String generateSubscriptionBill(User user, UserSubscription subscription) {
        String fileName = "bill-subscription-" + subscription.getId() + "-" + LocalDate.now() + ".pdf";
        fileStoragePort.ensureDirectory(BILLS_DIRECTORY);
        EnterpriseInformation enterpriseInformation = loadEnterpriseInformation();
        ReceiptData receiptData = buildReceiptData(user, subscription);

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float currentY = PAGE_HEIGHT - 31f;
                currentY = drawTopBand(document, contentStream, receiptData, currentY);
                currentY = drawPartyColumns(contentStream, enterpriseInformation, receiptData, currentY - 8f);
                currentY = drawPaidSummary(contentStream, receiptData, currentY - 12f);
                currentY = drawDescriptionTable(contentStream, receiptData, currentY - 18f);
                drawPaymentHistory(contentStream, receiptData, currentY - 20f);
                drawFooter(contentStream);
            }

            document.save(outputStream);
            return fileStoragePort.store(BILLS_DIRECTORY, fileName, outputStream.toByteArray());
        } catch (IOException e) {
            log.warn("Could not generate bill for subscriptionId={}", subscription.getId(), e);
            throw new TechnicalException("Could not generate subscription bill", e);
        }
    }

    private float drawTopBand(PDDocument document, PDPageContentStream contentStream, ReceiptData receiptData, float topY) throws IOException {
        writeText(contentStream, "Receipt", PAGE_MARGIN, topY, FONT_BOLD, 22f);

        PDImageXObject logo = tryLoadLogo(document);
        if (logo != null) {
            contentStream.drawImage(logo, PAGE_WIDTH - PAGE_MARGIN - HEADER_LOGO_WIDTH, topY - 18f, HEADER_LOGO_WIDTH, HEADER_LOGO_HEIGHT);
        }

        float labelX = PAGE_MARGIN;
        float valueX = PAGE_MARGIN + 104f;
        float metaY = topY - 32f;

        metaY = writeKeyValue(contentStream, "Invoice number", receiptData.invoiceNumber(), labelX, valueX, metaY);
        metaY = writeKeyValue(contentStream, "Receipt number", receiptData.receiptNumber(), labelX, valueX, metaY);
        writeKeyValue(contentStream, "Date paid", receiptData.datePaidLabel(), labelX, valueX, metaY);

        return topY - 82f;
    }

    private float drawPartyColumns(PDPageContentStream contentStream,
                                   EnterpriseInformation enterpriseInformation,
                                   ReceiptData receiptData,
                                   float startY) throws IOException {
        List<String> companyLines = new ArrayList<>();
        companyLines.add(enterpriseInformation.name());
        companyLines.addAll(wrapText(enterpriseInformation.address(), 28));
        companyLines.add(enterpriseInformation.phoneNumber());
        companyLines.add(enterpriseInformation.email());

        List<String> billedLines = new ArrayList<>();
        billedLines.add(receiptData.customerName());
        billedLines.addAll(wrapText(receiptData.customerAddress(), 28));
        billedLines.add(receiptData.customerEmail());

        float leftEnd = drawParagraph(contentStream, companyLines, PAGE_MARGIN, startY, null);
        float rightEnd = drawParagraph(contentStream, billedLines, PAGE_MARGIN + 210f, startY, "Bill to");
        return Math.min(leftEnd, rightEnd) - 18f;
    }

    private float drawPaidSummary(PDPageContentStream contentStream, ReceiptData receiptData, float startY) throws IOException {
        float y = startY;
        writeText(contentStream, receiptData.paidSummary(), PAGE_MARGIN, y, FONT_BOLD, 14f);
        y -= 21f;

        for (String line : wrapText(
                "This receipt confirms the successful payment of your subscription. Please keep this document for your records.",
                88
        )) {
            writeText(contentStream, line, PAGE_MARGIN, y, FONT_REGULAR, BODY_FONT_SIZE);
            y -= 13f;
        }

        y -= 6f;
        drawDivider(contentStream, y);
        y -= 14f;
        writeText(contentStream, "CONTACT", PAGE_MARGIN, y, FONT_REGULAR, SMALL_FONT_SIZE);
        y -= 12f;
        writeText(contentStream, "Questions about this receipt? Reach out using the company details above.", PAGE_MARGIN, y, FONT_REGULAR, BODY_FONT_SIZE);
        return y - 18f;
    }

    private float drawDescriptionTable(PDPageContentStream contentStream, ReceiptData receiptData, float startY) throws IOException {
        float tableRight = PAGE_MARGIN + CONTENT_WIDTH;
        float y = startY;

        float descriptionX = PAGE_MARGIN;
        float qtyX = PAGE_MARGIN + 355f;
        float unitPriceX = PAGE_MARGIN + 395f;
        float taxX = PAGE_MARGIN + 468f;
        float amountX = PAGE_MARGIN + 512f;

        writeText(contentStream, "Description", descriptionX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        writeText(contentStream, "Qty", qtyX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        writeText(contentStream, "Unit price", unitPriceX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        writeText(contentStream, "Tax", taxX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        writeText(contentStream, "Amount", amountX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        y -= 10f;
        drawDivider(contentStream, y);
        y -= 16f;

        writeText(contentStream, receiptData.planTitle(), descriptionX, y, FONT_REGULAR, BODY_FONT_SIZE);
        writeText(contentStream, "1", qtyX + 8f, y, FONT_REGULAR, BODY_FONT_SIZE);
        writeText(contentStream, receiptData.subtotalLabel(), unitPriceX, y, FONT_REGULAR, BODY_FONT_SIZE);
        writeText(contentStream, receiptData.taxRateLabel(), taxX, y, FONT_REGULAR, BODY_FONT_SIZE);
        writeText(contentStream, receiptData.subtotalLabel(), amountX, y, FONT_REGULAR, BODY_FONT_SIZE);
        y -= 15f;
        writeText(contentStream, receiptData.periodLabel(), descriptionX, y, FONT_REGULAR, BODY_FONT_SIZE);

        float totalsLabelX = PAGE_MARGIN + 267f;
        float totalsAmountRightX = tableRight - 2f;
        float totalsY = y - 22f;
        totalsY = writeSummaryLine(contentStream, "Subtotal", receiptData.subtotalLabel(), totalsLabelX, totalsAmountRightX, totalsY, false);
        totalsY = writeSummaryLine(contentStream, "Total excluding tax", receiptData.subtotalLabel(), totalsLabelX, totalsAmountRightX, totalsY, false);
        totalsY = writeSummaryLine(contentStream, receiptData.taxSummaryLabel(), receiptData.taxAmountLabel(), totalsLabelX, totalsAmountRightX, totalsY, false);
        totalsY = writeSummaryLine(contentStream, "Total", receiptData.totalLabel(), totalsLabelX, totalsAmountRightX, totalsY, false);
        return writeSummaryLine(contentStream, "Amount paid", receiptData.totalLabel(), totalsLabelX, totalsAmountRightX, totalsY, true) - 24f;
    }

    private void drawPaymentHistory(PDPageContentStream contentStream, ReceiptData receiptData, float startY) throws IOException {
        float y = startY;
        writeText(contentStream, "Payment history", PAGE_MARGIN, y, FONT_BOLD, 14f);
        y -= 26f;

        float methodX = PAGE_MARGIN;
        float dateX = PAGE_MARGIN + 277f;
        float amountX = PAGE_MARGIN + 374f;
        float receiptX = PAGE_MARGIN + 480f;

        writeText(contentStream, "Payment method", methodX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        writeText(contentStream, "Date", dateX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        writeText(contentStream, "Amount paid", amountX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        writeText(contentStream, "Receipt number", receiptX, y, FONT_REGULAR, SMALL_FONT_SIZE);
        y -= 10f;
        drawDivider(contentStream, y);
        y -= 16f;

        writeText(contentStream, receiptData.paymentMethodLabel(), methodX, y, FONT_REGULAR, BODY_FONT_SIZE);
        writeText(contentStream, receiptData.datePaidLabel(), dateX, y, FONT_REGULAR, BODY_FONT_SIZE);
        writeText(contentStream, receiptData.totalLabel(), amountX, y, FONT_REGULAR, BODY_FONT_SIZE);
        writeAlignedRightText(contentStream, receiptData.receiptNumber(), PAGE_MARGIN + CONTENT_WIDTH, y, FONT_REGULAR, BODY_FONT_SIZE);
    }

    private void drawFooter(PDPageContentStream contentStream) throws IOException {
        float lineY = 58f;
        contentStream.setStrokingColor(214f / 255f, 214f / 255f, 214f / 255f);
        contentStream.moveTo(PAGE_MARGIN, lineY);
        contentStream.lineTo(PAGE_MARGIN + CONTENT_WIDTH, lineY);
        contentStream.stroke();
        writeAlignedRightText(contentStream, "Page 1 of 1", PAGE_MARGIN + CONTENT_WIDTH, 38f, FONT_REGULAR, SMALL_FONT_SIZE);
    }

    private float drawParagraph(PDPageContentStream contentStream, List<String> lines, float x, float startY, String title) throws IOException {
        float y = startY;
        if (title != null) {
            writeText(contentStream, title, x, y, FONT_BOLD, BODY_FONT_SIZE);
            y -= 15f;
        }
        for (String line : lines) {
            writeText(contentStream, line, x, y, FONT_REGULAR, BODY_FONT_SIZE);
            y -= 13f;
        }
        return y;
    }

    private float writeKeyValue(PDPageContentStream contentStream, String key, String value, float keyX, float valueX, float y) throws IOException {
        writeText(contentStream, key, keyX, y, FONT_BOLD, BODY_FONT_SIZE);
        writeText(contentStream, value, valueX, y, FONT_REGULAR, BODY_FONT_SIZE);
        return y - 15f;
    }

    private float writeSummaryLine(PDPageContentStream contentStream,
                                   String label,
                                   String amount,
                                   float labelX,
                                   float amountRightX,
                                   float y,
                                   boolean bold) throws IOException {
        PDFont font = bold ? FONT_BOLD : FONT_REGULAR;
        contentStream.setStrokingColor(228f / 255f, 228f / 255f, 228f / 255f);
        contentStream.moveTo(labelX, y + 5f);
        contentStream.lineTo(amountRightX, y + 5f);
        contentStream.stroke();
        writeText(contentStream, label, labelX, y - 6f, font, BODY_FONT_SIZE);
        writeAlignedRightText(contentStream, amount, amountRightX, y - 6f, font, BODY_FONT_SIZE);
        return y - 18f;
    }

    private void drawDivider(PDPageContentStream contentStream, float y) throws IOException {
        contentStream.setStrokingColor(44f / 255f, 44f / 255f, 44f / 255f);
        contentStream.moveTo(PAGE_MARGIN, y);
        contentStream.lineTo(PAGE_MARGIN + CONTENT_WIDTH, y);
        contentStream.stroke();
    }

    private void writeAlignedRightText(PDPageContentStream contentStream, String text, float rightX, float y, PDFont font, float fontSize)
            throws IOException {
        float width = font.getStringWidth(text) / 1000f * fontSize;
        writeText(contentStream, text, rightX - width, y, font, fontSize);
    }

    private void writeText(PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize)
            throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(sanitizePdfText(text));
        contentStream.endText();
    }

    private EnterpriseInformation loadEnterpriseInformation() {
        return new EnterpriseInformation(
                readEnterpriseValue(ENTERPRISE_NAME),
                readEnterpriseValue(ENTERPRISE_ADDRESS),
                readEnterpriseValue(ENTERPRISE_PHONE_NUMBER),
                readEnterpriseValue(ENTERPRISE_EMAIL)
        );
    }

    private String readEnterpriseValue(String code) {
        AppConfiguration configuration = appConfigurationPersistencePort.findByCategoryAndCode(AppConfigurationCategory.ENTERPRISE, code)
                .filter(AppConfiguration::isActive)
                .orElseThrow(() -> new TechnicalException("Missing active enterprise configuration for code: " + code));
        return configuration.getLabel();
    }

    private ReceiptData buildReceiptData(User user, UserSubscription subscription) {
        LocalDate paidDate = subscription.getCreationDate() == null
                ? LocalDate.now()
                : subscription.getCreationDate().atZone(ZoneId.systemDefault()).toLocalDate();
        BigDecimal subtotal = subscription.getPricePaid() == null ? BigDecimal.ZERO : subscription.getPricePaid();
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(taxAmount);

        return new ReceiptData(
                "SUB-" + subscription.getId(),
                emptyIfNull(subscription.getExternalPaymentId()),
                formatLongDate(paidDate),
                formatMoney(total, subscription.getCurrencyCode()) + " paid on " + formatLongDate(paidDate),
                user.getUserInfo().firstName() + " " + user.getUserInfo().lastName(),
                emptyIfNull(user.getUserInfo().address()),
                user.getUserCredentials().getEmail(),
                subscription.getPlanTitle(),
                formatPeriod(subscription.getStartDate(), subscription.getEndDate()),
                formatMoney(subtotal, subscription.getCurrencyCode()),
                "0%",
                "Tax (0% on " + formatMoney(subtotal, subscription.getCurrencyCode()) + ")",
                formatMoney(taxAmount, subscription.getCurrencyCode()),
                formatMoney(total, subscription.getCurrencyCode()),
                prettifyPaymentMode(subscription.getPaymentMode())
        );
    }

    private String formatPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return "-";
        }
        if (startDate == null) {
            return LONG_DATE_FORMATTER.format(endDate);
        }
        if (endDate == null) {
            return SHORT_MONTH_FORMATTER.format(startDate) + " - Lifetime";
        }
        return SHORT_MONTH_FORMATTER.format(startDate) + " - " + LONG_DATE_FORMATTER.format(endDate);
    }

    private String formatLongDate(LocalDate date) {
        return date == null ? "-" : LONG_DATE_FORMATTER.format(date);
    }

    private String formatMoney(BigDecimal amount, String currencyCode) {
        BigDecimal normalized = amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP);
        return new DecimalFormat("0.00").format(normalized) + " " + emptyIfNull(currencyCode);
    }

    private String prettifyPaymentMode(String paymentMode) {
        return switch (emptyIfNull(paymentMode).toUpperCase(Locale.ROOT)) {
            case "STRIPE" -> "Card payment via Stripe";
            case "PAYPAL" -> "PayPal";
            default -> emptyIfNull(paymentMode);
        };
    }

    private List<String> wrapText(String text, int maxLength) {
        String sanitized = emptyIfNull(text);
        if (sanitized.length() <= maxLength) {
            return List.of(sanitized);
        }

        List<String> lines = new ArrayList<>();
        String remaining = sanitized;
        while (remaining.length() > maxLength) {
            int split = remaining.lastIndexOf(' ', maxLength);
            if (split <= 0) {
                split = maxLength;
            }
            lines.add(remaining.substring(0, split).trim());
            remaining = remaining.substring(split).trim();
        }
        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }
        return lines;
    }

    private byte[] loadLogoBytes() {
        try (InputStream inputStream = new ClassPathResource(LOGO_RESOURCE_PATH).getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new TechnicalException("Could not load enterprise logo resource", e);
        }
    }

    private PDImageXObject tryLoadLogo(PDDocument document) {
        try {
            return PDImageXObject.createFromByteArray(document, loadLogoBytes(), "enterprise-logo");
        } catch (RuntimeException | IOException e) {
            log.warn("Could not load enterprise logo for receipt PDF, continuing without logo", e);
            return null;
        }
    }

    private String emptyIfNull(String value) {
        return StringUtils.isBlank(value) ? "-" : value;
    }

    private String sanitizePdfText(String value) {
        return emptyIfNull(value)
                .replace("\u2019", "'")
                .replace("\u2018", "'")
                .replace("\u201C", "\"")
                .replace("\u201D", "\"")
                .replace("\u20AC", "EUR ");
    }

    private record EnterpriseInformation(
            String name,
            String address,
            String phoneNumber,
            String email
    ) {}

    private record ReceiptData(
            String invoiceNumber,
            String receiptNumber,
            String datePaidLabel,
            String paidSummary,
            String customerName,
            String customerAddress,
            String customerEmail,
            String planTitle,
            String periodLabel,
            String subtotalLabel,
            String taxRateLabel,
            String taxSummaryLabel,
            String taxAmountLabel,
            String totalLabel,
            String paymentMethodLabel
    ) {}
}
