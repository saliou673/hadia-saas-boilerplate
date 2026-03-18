package com.hadiasaas.infrastructure.adapter.out.billing;

import com.hadiasaas.domain.exceptions.TechnicalException;
import com.hadiasaas.domain.models.enterpriseprofile.EnterpriseProfile;
import com.hadiasaas.domain.models.subscription.UserSubscription;
import com.hadiasaas.domain.models.user.User;
import com.hadiasaas.domain.ports.out.FileStoragePort;
import com.hadiasaas.domain.ports.out.SubscriptionBillPort;
import com.hadiasaas.domain.ports.out.persistenceport.EnterpriseProfilePersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.context.MessageSource;
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
import java.time.format.FormatStyle;
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
    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.LETTER.getHeight();
    private static final float PAGE_MARGIN = 28f;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (PAGE_MARGIN * 2);
    private static final float BODY_FONT_SIZE = 10.5f;
    private static final float SMALL_FONT_SIZE = 8.5f;
    private static final float HEADER_LOGO_WIDTH = 38f;
    private static final float HEADER_LOGO_HEIGHT = 38f;
    private static final String LOGO_RESOURCE_PATH = "billing/enterprise-logo.png";
    private static final String REGULAR_FONT_RESOURCE_PATH = "billing/fonts/NotoSans-Regular.ttf";
    private static final String BOLD_FONT_RESOURCE_PATH = "billing/fonts/NotoSans-Bold.ttf";

    private final FileStoragePort fileStoragePort;
    private final EnterpriseProfilePersistencePort enterpriseProfilePersistencePort;
    private final MessageSource messageSource;

    @Override
    public String generateSubscriptionBill(User user, UserSubscription subscription) {
        String fileName = "bill-subscription-" + subscription.getId() + "-" + LocalDate.now() + ".pdf";
        fileStoragePort.ensureDirectory(BILLS_DIRECTORY);
        EnterpriseInformation enterpriseInformation = loadEnterpriseInformation();
        Locale locale = resolveLocale(user);
        ReceiptData receiptData = buildReceiptData(user, subscription, locale);

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ReceiptFonts fonts = loadFonts(document);
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float currentY = PAGE_HEIGHT - 31f;
                currentY = drawTopBand(document, contentStream, receiptData, fonts, currentY);
                currentY = drawPartyColumns(contentStream, enterpriseInformation, receiptData, fonts, currentY - 8f);
                currentY = drawPaidSummary(contentStream, receiptData, fonts, currentY - 12f);
                currentY = drawDescriptionTable(contentStream, receiptData, fonts, currentY - 18f);
                drawPaymentHistory(contentStream, receiptData, fonts, currentY - 20f);
                drawFooter(contentStream, receiptData, fonts);
            }

            document.save(outputStream);
            return fileStoragePort.store(BILLS_DIRECTORY, fileName, outputStream.toByteArray());
        } catch (IOException e) {
            log.warn("Could not generate bill for subscriptionId={}", subscription.getId(), e);
            throw new TechnicalException("Could not generate subscription bill", e);
        }
    }

    private float drawTopBand(PDDocument document, PDPageContentStream contentStream, ReceiptData receiptData, ReceiptFonts fonts, float topY) throws IOException {
        writeText(contentStream, receiptData.receiptTitle(), PAGE_MARGIN, topY, fonts.bold(), 22f);

        PDImageXObject logo = tryLoadLogo(document);
        if (logo != null) {
            contentStream.drawImage(logo, PAGE_WIDTH - PAGE_MARGIN - HEADER_LOGO_WIDTH, topY - 18f, HEADER_LOGO_WIDTH, HEADER_LOGO_HEIGHT);
        }

        float labelX = PAGE_MARGIN;
        float valueX = PAGE_MARGIN + 104f;
        float metaY = topY - 32f;

        metaY = writeKeyValue(contentStream, receiptData.invoiceNumberLabel(), receiptData.invoiceNumber(), labelX, valueX, metaY, fonts);
        metaY = writeKeyValue(contentStream, receiptData.receiptNumberLabel(), receiptData.receiptNumber(), labelX, valueX, metaY, fonts);
        writeKeyValue(contentStream, receiptData.datePaidFieldLabel(), receiptData.datePaidLabel(), labelX, valueX, metaY, fonts);

        return topY - 82f;
    }

    private float drawPartyColumns(PDPageContentStream contentStream,
                                   EnterpriseInformation enterpriseInformation,
                                   ReceiptData receiptData,
                                   ReceiptFonts fonts,
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

        float leftEnd = drawParagraph(contentStream, companyLines, PAGE_MARGIN, startY, null, fonts);
        float rightEnd = drawParagraph(contentStream, billedLines, PAGE_MARGIN + 210f, startY, receiptData.billToLabel(), fonts);
        return Math.min(leftEnd, rightEnd) - 18f;
    }

    private float drawPaidSummary(PDPageContentStream contentStream, ReceiptData receiptData, ReceiptFonts fonts, float startY) throws IOException {
        float y = startY;
        writeText(contentStream, receiptData.paidSummary(), PAGE_MARGIN, y, fonts.bold(), 14f);
        y -= 21f;

        for (String line : wrapText(receiptData.paidDescription(), 88)) {
            writeText(contentStream, line, PAGE_MARGIN, y, fonts.regular(), BODY_FONT_SIZE);
            y -= 13f;
        }

        y -= 6f;
        drawDivider(contentStream, y);
        y -= 14f;
        writeText(contentStream, receiptData.contactLabel(), PAGE_MARGIN, y, fonts.regular(), SMALL_FONT_SIZE);
        y -= 12f;
        writeText(contentStream, receiptData.contactText(), PAGE_MARGIN, y, fonts.regular(), BODY_FONT_SIZE);
        return y - 18f;
    }

    private float drawDescriptionTable(PDPageContentStream contentStream, ReceiptData receiptData, ReceiptFonts fonts, float startY) throws IOException {
        float tableRight = PAGE_MARGIN + CONTENT_WIDTH;
        float y = startY;

        float descriptionX = PAGE_MARGIN;
        float qtyX = PAGE_MARGIN + 355f;
        float unitPriceX = PAGE_MARGIN + 395f;
        float taxX = PAGE_MARGIN + 468f;
        float amountX = PAGE_MARGIN + 512f;

        writeText(contentStream, receiptData.descriptionLabel(), descriptionX, y, fonts.regular(), SMALL_FONT_SIZE);
        writeText(contentStream, receiptData.quantityLabel(), qtyX, y, fonts.regular(), SMALL_FONT_SIZE);
        writeText(contentStream, receiptData.unitPriceLabel(), unitPriceX, y, fonts.regular(), SMALL_FONT_SIZE);
        writeText(contentStream, receiptData.taxLabel(), taxX, y, fonts.regular(), SMALL_FONT_SIZE);
        writeText(contentStream, receiptData.amountLabel(), amountX, y, fonts.regular(), SMALL_FONT_SIZE);
        y -= 10f;
        drawDivider(contentStream, y);
        y -= 16f;

        writeText(contentStream, receiptData.planTitle(), descriptionX, y, fonts.regular(), BODY_FONT_SIZE);
        writeText(contentStream, "1", qtyX + 8f, y, fonts.regular(), BODY_FONT_SIZE);
        writeText(contentStream, receiptData.subtotalLabel(), unitPriceX, y, fonts.regular(), BODY_FONT_SIZE);
        writeText(contentStream, receiptData.taxRateLabel(), taxX, y, fonts.regular(), BODY_FONT_SIZE);
        writeText(contentStream, receiptData.subtotalLabel(), amountX, y, fonts.regular(), BODY_FONT_SIZE);
        y -= 15f;
        writeText(contentStream, receiptData.periodLabel(), descriptionX, y, fonts.regular(), BODY_FONT_SIZE);

        float totalsLabelX = PAGE_MARGIN + 267f;
        float totalsAmountRightX = tableRight - 2f;
        float totalsY = y - 22f;
        totalsY = writeSummaryLine(contentStream, receiptData.subtotalFieldLabel(), receiptData.subtotalLabel(), totalsLabelX, totalsAmountRightX, totalsY, false, fonts);
        totalsY = writeSummaryLine(contentStream, receiptData.totalExcludingTaxLabel(), receiptData.subtotalLabel(), totalsLabelX, totalsAmountRightX, totalsY, false, fonts);
        totalsY = writeSummaryLine(contentStream, receiptData.taxSummaryLabel(), receiptData.taxAmountLabel(), totalsLabelX, totalsAmountRightX, totalsY, false, fonts);
        totalsY = writeSummaryLine(contentStream, receiptData.totalFieldLabel(), receiptData.totalLabel(), totalsLabelX, totalsAmountRightX, totalsY, false, fonts);
        return writeSummaryLine(contentStream, receiptData.amountPaidLabel(), receiptData.totalLabel(), totalsLabelX, totalsAmountRightX, totalsY, true, fonts) - 24f;
    }

    private void drawPaymentHistory(PDPageContentStream contentStream, ReceiptData receiptData, ReceiptFonts fonts, float startY) throws IOException {
        float y = startY;
        writeText(contentStream, receiptData.paymentHistoryTitle(), PAGE_MARGIN, y, fonts.bold(), 14f);
        y -= 26f;

        float methodX = PAGE_MARGIN;
        float dateX = PAGE_MARGIN + 277f;
        float amountX = PAGE_MARGIN + 374f;
        float receiptX = PAGE_MARGIN + 480f;

        writeText(contentStream, receiptData.paymentMethodLabelTitle(), methodX, y, fonts.regular(), SMALL_FONT_SIZE);
        writeText(contentStream, receiptData.dateLabel(), dateX, y, fonts.regular(), SMALL_FONT_SIZE);
        writeText(contentStream, receiptData.amountPaidLabel(), amountX, y, fonts.regular(), SMALL_FONT_SIZE);
        writeText(contentStream, receiptData.receiptNumberLabel(), receiptX, y, fonts.regular(), SMALL_FONT_SIZE);
        y -= 10f;
        drawDivider(contentStream, y);
        y -= 16f;

        writeText(contentStream, receiptData.paymentMethodLabel(), methodX, y, fonts.regular(), BODY_FONT_SIZE);
        writeText(contentStream, receiptData.datePaidLabel(), dateX, y, fonts.regular(), BODY_FONT_SIZE);
        writeText(contentStream, receiptData.totalLabel(), amountX, y, fonts.regular(), BODY_FONT_SIZE);
        writeAlignedRightText(contentStream, receiptData.receiptNumber(), PAGE_MARGIN + CONTENT_WIDTH, y, fonts.regular(), BODY_FONT_SIZE);
    }

    private void drawFooter(PDPageContentStream contentStream, ReceiptData receiptData, ReceiptFonts fonts) throws IOException {
        float lineY = 58f;
        contentStream.setStrokingColor(214f / 255f, 214f / 255f, 214f / 255f);
        contentStream.moveTo(PAGE_MARGIN, lineY);
        contentStream.lineTo(PAGE_MARGIN + CONTENT_WIDTH, lineY);
        contentStream.stroke();
        writeAlignedRightText(contentStream, localize("receipt.page", receiptData.locale(), 1, 1), PAGE_MARGIN + CONTENT_WIDTH, 38f, fonts.regular(), SMALL_FONT_SIZE);
    }

    private float drawParagraph(PDPageContentStream contentStream, List<String> lines, float x, float startY, String title, ReceiptFonts fonts) throws IOException {
        float y = startY;
        if (title != null) {
            writeText(contentStream, title, x, y, fonts.bold(), BODY_FONT_SIZE);
            y -= 15f;
        }
        for (String line : lines) {
            writeText(contentStream, line, x, y, fonts.regular(), BODY_FONT_SIZE);
            y -= 13f;
        }
        return y;
    }

    private float writeKeyValue(PDPageContentStream contentStream, String key, String value, float keyX, float valueX, float y, ReceiptFonts fonts) throws IOException {
        writeText(contentStream, key, keyX, y, fonts.bold(), BODY_FONT_SIZE);
        writeText(contentStream, value, valueX, y, fonts.regular(), BODY_FONT_SIZE);
        return y - 15f;
    }

    private float writeSummaryLine(PDPageContentStream contentStream,
                                   String label,
                                   String amount,
                                   float labelX,
                                   float amountRightX,
                                   float y,
                                   boolean bold,
                                   ReceiptFonts fonts) throws IOException {
        PDFont font = bold ? fonts.bold() : fonts.regular();
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
        String sanitizedText = sanitizePdfText(text);
        float width = font.getStringWidth(sanitizedText) / 1000f * fontSize;
        writeText(contentStream, sanitizedText, rightX - width, y, font, fontSize);
    }

    private void writeText(PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize)
            throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(sanitizePdfText(text));
        contentStream.endText();
    }

    private ReceiptFonts loadFonts(PDDocument document) {
        try (InputStream regularStream = new ClassPathResource(REGULAR_FONT_RESOURCE_PATH).getInputStream();
             InputStream boldStream = new ClassPathResource(BOLD_FONT_RESOURCE_PATH).getInputStream()) {
            return new ReceiptFonts(
                    PDType0Font.load(document, regularStream, true),
                    PDType0Font.load(document, boldStream, true)
            );
        } catch (IOException e) {
            throw new TechnicalException("Could not load receipt fonts", e);
        }
    }

    private EnterpriseInformation loadEnterpriseInformation() {
        EnterpriseProfile profile = enterpriseProfilePersistencePort.find()
                .orElseThrow(() -> new TechnicalException("Enterprise profile not configured"));
        return new EnterpriseInformation(
                emptyIfNull(profile.getCompanyName()),
                buildAddress(profile),
                emptyIfNull(profile.getPhoneNumber()),
                emptyIfNull(profile.getEmail())
        );
    }

    private String buildAddress(EnterpriseProfile profile) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(profile.getAddressLine1())) {
            parts.add(profile.getAddressLine1());
        }
        if (StringUtils.isNotBlank(profile.getAddressLine2())) {
            parts.add(profile.getAddressLine2());
        }
        if (StringUtils.isNotBlank(profile.getCity()) && StringUtils.isNotBlank(profile.getPostalCode())) {
            parts.add(profile.getPostalCode() + " " + profile.getCity());
        } else if (StringUtils.isNotBlank(profile.getCity())) {
            parts.add(profile.getCity());
        } else if (StringUtils.isNotBlank(profile.getPostalCode())) {
            parts.add(profile.getPostalCode());
        }
        return parts.isEmpty() ? "-" : String.join(", ", parts);
    }

    private ReceiptData buildReceiptData(User user, UserSubscription subscription, Locale locale) {
        LocalDate paidDate = subscription.getCreationDate() == null
                ? LocalDate.now()
                : subscription.getCreationDate().atZone(ZoneId.systemDefault()).toLocalDate();
        BigDecimal total = subscription.getPricePaid() == null ? BigDecimal.ZERO : subscription.getPricePaid();
        BigDecimal taxAmount = subscription.getTaxAmount() == null ? BigDecimal.ZERO : subscription.getTaxAmount();
        BigDecimal subtotal = total.subtract(taxAmount);
        BigDecimal taxRate = subscription.getTaxRate() == null ? BigDecimal.ZERO : subscription.getTaxRate();

        return new ReceiptData(
                locale,
                "SUB-" + subscription.getId(),
                emptyIfNull(subscription.getExternalPaymentId()),
                formatLongDate(paidDate, locale),
                localize("receipt.paid.summary", locale, formatMoney(total, subscription.getCurrencyCode()), formatLongDate(paidDate, locale)),
                localize("receipt.paid.description", locale),
                user.getUserInfo().firstName() + " " + user.getUserInfo().lastName(),
                emptyIfNull(user.getUserInfo().address()),
                user.getUserCredentials().getEmail(),
                subscription.getPlanTitle(),
                formatPeriod(subscription.getStartDate(), subscription.getEndDate(), locale),
                formatMoney(subtotal, subscription.getCurrencyCode()),
                formatTaxRate(taxRate),
                localize("receipt.tax.summary", locale, formatTaxRate(taxRate), formatMoney(subtotal, subscription.getCurrencyCode())),
                formatMoney(taxAmount, subscription.getCurrencyCode()),
                formatMoney(total, subscription.getCurrencyCode()),
                prettifyPaymentMode(subscription.getPaymentMode(), locale),
                localize("receipt.title", locale),
                localize("receipt.invoice-number", locale),
                localize("receipt.receipt-number", locale),
                localize("receipt.date-paid", locale),
                localize("receipt.bill-to", locale),
                localize("receipt.contact", locale),
                localize("receipt.contact-text", locale),
                localize("receipt.description", locale),
                localize("receipt.qty", locale),
                localize("receipt.unit-price", locale),
                localize("receipt.tax", locale),
                localize("receipt.amount", locale),
                localize("receipt.subtotal", locale),
                localize("receipt.total-excluding-tax", locale),
                localize("receipt.total", locale),
                localize("receipt.amount-paid", locale),
                localize("receipt.payment-history", locale),
                localize("receipt.payment-method", locale),
                localize("receipt.date", locale)
        );
    }

    private String formatPeriod(LocalDate startDate, LocalDate endDate, Locale locale) {
        if (startDate == null && endDate == null) {
            return "-";
        }
        if (startDate == null) {
            return formatLongDate(endDate, locale);
        }
        if (endDate == null) {
            return formatShortDate(startDate, locale) + " - " + localize("receipt.lifetime", locale);
        }
        return formatShortDate(startDate, locale) + " - " + formatLongDate(endDate, locale);
    }

    private String formatLongDate(LocalDate date, Locale locale) {
        return date == null ? "-" : DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale).format(date);
    }

    private String formatShortDate(LocalDate date, Locale locale) {
        return date == null ? "-" : DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).format(date);
    }

    private String formatMoney(BigDecimal amount, String currencyCode) {
        BigDecimal normalized = amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP);
        return new DecimalFormat("0.00").format(normalized) + " " + emptyIfNull(currencyCode);
    }

    private String prettifyPaymentMode(String paymentMode, Locale locale) {
        return switch (emptyIfNull(paymentMode).toUpperCase(Locale.ROOT)) {
            case "STRIPE" -> localize("receipt.payment-mode.stripe", locale);
            case "PAYPAL" -> "PayPal";
            default -> emptyIfNull(paymentMode);
        };
    }

    private Locale resolveLocale(User user) {
        String languageKey = user.getUserInfo() == null ? null : user.getUserInfo().languageKey();
        return StringUtils.isBlank(languageKey) ? Locale.ENGLISH : Locale.forLanguageTag(languageKey);
    }

    private String localize(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }

    private String formatTaxRate(BigDecimal taxRate) {
        BigDecimal normalized = taxRate == null ? BigDecimal.ZERO : taxRate.stripTrailingZeros();
        return normalized.toPlainString() + "%";
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
                .replace("\uFFFD", "")
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

    private record ReceiptFonts(
            PDFont regular,
            PDFont bold
    ) {}

    private record ReceiptData(
            Locale locale,
            String invoiceNumber,
            String receiptNumber,
            String datePaidLabel,
            String paidSummary,
            String paidDescription,
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
            String paymentMethodLabel,
            String receiptTitle,
            String invoiceNumberLabel,
            String receiptNumberLabel,
            String datePaidFieldLabel,
            String billToLabel,
            String contactLabel,
            String contactText,
            String descriptionLabel,
            String quantityLabel,
            String unitPriceLabel,
            String taxLabel,
            String amountLabel,
            String subtotalFieldLabel,
            String totalExcludingTaxLabel,
            String totalFieldLabel,
            String amountPaidLabel,
            String paymentHistoryTitle,
            String paymentMethodLabelTitle,
            String dateLabel
    ) {}
}
