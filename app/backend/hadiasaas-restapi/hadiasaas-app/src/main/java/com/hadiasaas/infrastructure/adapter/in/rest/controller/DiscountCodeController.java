package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.enumerations.DiscountType;
import com.hadiasaas.domain.exceptions.DiscountCodeNotFoundException;
import com.hadiasaas.domain.exceptions.InvalidDiscountCodeException;
import com.hadiasaas.domain.models.discountcode.DiscountCode;
import com.hadiasaas.domain.ports.in.DiscountCodeQueryUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.DiscountCodeStatusResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Public REST controller for discount code status checks.
 */
@Validated
@RestController
@Tag(name = "Discount codes")
@RequestMapping("/api/v1/discount-codes")
@RequiredArgsConstructor
public class DiscountCodeController {

    private final DiscountCodeQueryUseCase discountCodeQueryUseCase;

    @GetMapping("/{code}/status")
    public DiscountCodeStatusResponse getStatus(@PathVariable String code) {
        try {
            DiscountCode discountCode = discountCodeQueryUseCase.getByCode(code);
            discountCode.validateForUse(LocalDate.now());
            return new DiscountCodeStatusResponse(
                    code,
                    true,
                    discountCode.getDiscountType(),
                    discountCode.getDiscountValue(),
                    discountCode.getDiscountType() == DiscountType.FIXED_AMOUNT ? discountCode.getCurrencyCode() : null,
                    discountCode.getExpirationDate()
            );
        } catch (DiscountCodeNotFoundException | InvalidDiscountCodeException ex) {
            return new DiscountCodeStatusResponse(code, false, null, null, null, null);
        }
    }
}
