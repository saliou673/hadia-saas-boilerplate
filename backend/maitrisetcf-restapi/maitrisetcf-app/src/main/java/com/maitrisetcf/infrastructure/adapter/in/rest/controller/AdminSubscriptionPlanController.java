package com.maitrisetcf.infrastructure.adapter.in.rest.controller;

import com.maitrisetcf.domain.models.query.PagedResult;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlanFilter;
import com.maitrisetcf.domain.ports.in.SubscriptionPlanQueryUseCase;
import com.maitrisetcf.domain.ports.in.SubscriptionPlanUseCase;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.SubscriptionPlanDtoMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.CreateSubscriptionPlanRequest;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.UpdateSubscriptionPlanRequest;
import com.maitrisetcf.infrastructure.adapter.out.query.PaginatedResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static com.maitrisetcf.util.PaginationConstants.DEFAULT_PAGE_SIZE_INT;

/**
 * REST controller for admin subscription plan management.
 */
@Validated
@RestController
@Tag(name = "Admin subscription plan management")
@RequestMapping("/api/v1/admin/plans")
@RequiredArgsConstructor
public class AdminSubscriptionPlanController {

    private final SubscriptionPlanUseCase subscriptionPlanUseCase;
    private final SubscriptionPlanQueryUseCase subscriptionPlanQueryUseCase;
    private final SubscriptionPlanDtoMapper subscriptionPlanDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('plan:create')")
    public SubscriptionPlanDTO create(@Valid @RequestBody CreateSubscriptionPlanRequest request) {
        return subscriptionPlanDtoMapper.toDTO(
                subscriptionPlanUseCase.create(
                        request.title(),
                        request.description(),
                        request.price(),
                        request.currencyCode(),
                        request.features() != null ? request.features() : Collections.emptyList(),
                        request.durationDays(),
                        Boolean.TRUE.equals(request.active()),
                        request.type()
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('plan:read')")
    public SubscriptionPlanDTO getById(@PathVariable Long id) {
        return subscriptionPlanDtoMapper.toDTO(subscriptionPlanUseCase.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('plan:read')")
    public PaginatedResult<SubscriptionPlanDTO> getAll(
            SubscriptionPlanFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PagedResult<SubscriptionPlan> result = subscriptionPlanQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, subscriptionPlanDtoMapper::toDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('plan:update')")
    public SubscriptionPlanDTO update(@PathVariable Long id, @Valid @RequestBody UpdateSubscriptionPlanRequest request) {
        return subscriptionPlanDtoMapper.toDTO(
                subscriptionPlanUseCase.update(
                        id,
                        request.title(),
                        request.description(),
                        request.price(),
                        request.currencyCode(),
                        request.features() != null ? request.features() : Collections.emptyList(),
                        request.durationDays(),
                        Boolean.TRUE.equals(request.active()),
                        request.type()
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('plan:delete')")
    public void delete(@PathVariable Long id) {
        subscriptionPlanUseCase.delete(id);
    }
}
