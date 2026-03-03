package com.maitrisetcf.infrastructure.adapter.in.rest.controller;

import com.maitrisetcf.domain.models.subscription.UserSubscription;
import com.maitrisetcf.domain.ports.in.SubscribeUseCase;
import com.maitrisetcf.domain.ports.in.UserSubscriptionQueryUseCase;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.UserSubscriptionDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.UserSubscriptionDtoMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.SubscribeRequest;
import com.maitrisetcf.infrastructure.adapter.out.query.PaginatedResult;
import com.maitrisetcf.util.PaginationConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user subscription operations.
 */
@Validated
@RestController
@Tag(name = "Subscriptions")
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserSubscriptionController {

    private final SubscribeUseCase subscribeUseCase;
    private final UserSubscriptionQueryUseCase userSubscriptionQueryUseCase;
    private final UserSubscriptionDtoMapper userSubscriptionDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSubscriptionDTO subscribe(@Valid @RequestBody SubscribeRequest request) {
        UserSubscription subscription = subscribeUseCase.subscribe(request.planId(), request.paymentMode(), request.billingFrequency());
        return userSubscriptionDtoMapper.toDTO(subscription);
    }

    @GetMapping("/me")
    public PaginatedResult<UserSubscriptionDTO> getMySubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE_INT + "") int size) {
        return new PaginatedResult<>(userSubscriptionQueryUseCase.findMySubscriptions(page, size), userSubscriptionDtoMapper::toDTO);
    }

    @PostMapping("/{id}/renew")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSubscriptionDTO renew(@PathVariable Long id) {
        return userSubscriptionDtoMapper.toDTO(subscribeUseCase.renew(id));
    }

    @PutMapping("/{id}/cancel")
    public UserSubscriptionDTO cancel(@PathVariable Long id) {
        return userSubscriptionDtoMapper.toDTO(subscribeUseCase.cancel(id, false));
    }
}
