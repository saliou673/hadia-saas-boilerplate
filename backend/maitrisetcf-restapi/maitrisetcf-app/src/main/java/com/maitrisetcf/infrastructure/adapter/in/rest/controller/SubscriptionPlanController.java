package com.maitrisetcf.infrastructure.adapter.in.rest.controller;

import com.maitrisetcf.domain.ports.in.SubscriptionPlanQueryUseCase;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.SubscriptionPlanDtoMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<SubscriptionPlanDTO> getAll() {
        return subscriptionPlanQueryUseCase.findAllActive()
                .stream()
                .map(subscriptionPlanDtoMapper::toDTO)
                .toList();
    }
}
