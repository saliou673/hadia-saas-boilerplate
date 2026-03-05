package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.ports.in.SubscriptionPlanQueryUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.SubscriptionPlanDtoMapper;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import com.hadiasaas.util.PaginationConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for public subscription plan listing.
 */
@RestController
@Tag(name = "Subscription plans")
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanQueryUseCase subscriptionPlanQueryUseCase;
    private final SubscriptionPlanDtoMapper subscriptionPlanDtoMapper;

    @GetMapping
    public PaginatedResult<SubscriptionPlanDTO> getSubscriptionPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE_INT + "") int size) {
        return new PaginatedResult<>(subscriptionPlanQueryUseCase.findAllActive(page, size), subscriptionPlanDtoMapper::toDTO);
    }
}
