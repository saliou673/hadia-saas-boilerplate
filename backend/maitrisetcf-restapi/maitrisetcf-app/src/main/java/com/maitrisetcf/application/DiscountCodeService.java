package com.maitrisetcf.application;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.enumerations.DiscountType;
import com.maitrisetcf.domain.exceptions.DiscountCodeAlreadyExistsException;
import com.maitrisetcf.domain.exceptions.DiscountCodeNotFoundException;
import com.maitrisetcf.domain.exceptions.InvalidCurrencyException;
import com.maitrisetcf.domain.exceptions.RequiredFieldException;
import com.maitrisetcf.domain.models.discountcode.DiscountCode;
import com.maitrisetcf.domain.ports.in.DiscountCodeUseCase;
import com.maitrisetcf.domain.ports.out.persistenceport.AppConfigurationPersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.DiscountCodePersistencePort;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Application service implementing {@link DiscountCodeUseCase}.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DiscountCodeService implements DiscountCodeUseCase {

    private final DiscountCodePersistencePort discountCodePersistencePort;
    private final AppConfigurationPersistencePort appConfigurationPersistencePort;

    @Override
    public DiscountCode create(
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            @Nullable String currencyCode,
            boolean active,
            @Nullable LocalDate expirationDate,
            @Nullable Integer maxUsages
    ) {
        log.debug("Creating discount code {}", code);
        if (discountCodePersistencePort.existsByCode(code)) {
            throw new DiscountCodeAlreadyExistsException("Discount code already exists: " + code);
        }

        DiscountCode discountCode = DiscountCode.create(
                code,
                discountType,
                discountValue,
                validateAndNormalizeCurrencyCode(discountType, currencyCode),
                active,
                expirationDate,
                maxUsages
        );
        return discountCodePersistencePort.save(discountCode);
    }

    @Override
    public DiscountCode update(
            Long id,
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            @Nullable String currencyCode,
            boolean active,
            @Nullable LocalDate expirationDate,
            @Nullable Integer maxUsages
    ) {
        log.debug("Updating discount code id={}", id);
        DiscountCode discountCode = discountCodePersistencePort.findById(id)
                .orElseThrow(() -> new DiscountCodeNotFoundException("Discount code not found with id: " + id));

        if (discountCodePersistencePort.existsByCodeAndIdNot(code, id)) {
            throw new DiscountCodeAlreadyExistsException("Discount code already exists: " + code);
        }

        discountCode.update(
                code,
                discountType,
                discountValue,
                validateAndNormalizeCurrencyCode(discountType, currencyCode),
                active,
                expirationDate,
                maxUsages
        );
        return discountCodePersistencePort.save(discountCode);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting discount code id={}", id);
        DiscountCode discountCode = discountCodePersistencePort.findById(id)
                .orElseThrow(() -> new DiscountCodeNotFoundException("Discount code not found with id: " + id));
        discountCodePersistencePort.remove(discountCode);
    }

    @Override
    public DiscountCode getById(Long id) {
        return discountCodePersistencePort.findById(id)
                .orElseThrow(() -> new DiscountCodeNotFoundException("Discount code not found with id: " + id));
    }

    private String validateAndNormalizeCurrencyCode(DiscountType discountType, @Nullable String currencyCode) {
        if (discountType == DiscountType.PERCENTAGE) {
            return null;
        }
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new RequiredFieldException("currencyCode is required for fixed amount discount codes");
        }
        if (!appConfigurationPersistencePort.existsActiveByCategoryAndCode(AppConfigurationCategory.CURRENCY, currencyCode)) {
            throw new InvalidCurrencyException("Currency code '" + currencyCode + "' is not a valid active currency");
        }
        return currencyCode;
    }
}
