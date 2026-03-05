package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.subscription.UserSubscription;
import com.hadiasaas.domain.models.subscription.UserSubscriptionFilter;
import com.hadiasaas.domain.ports.in.SubscribeUseCase;
import com.hadiasaas.domain.ports.in.UserSubscriptionQueryUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.UserSubscriptionDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.UserSubscriptionDtoMapper;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hadiasaas.util.PaginationConstants.DEFAULT_PAGE_SIZE_INT;

/**
 * REST controller for admin subscription management.
 */
@Validated
@RestController
@Tag(name = "Admin subscription management")
@RequestMapping("/api/v1/admin/subscriptions")
@RequiredArgsConstructor
public class AdminUserSubscriptionController {

    private final SubscribeUseCase subscribeUseCase;
    private final UserSubscriptionQueryUseCase userSubscriptionQueryUseCase;
    private final UserSubscriptionDtoMapper userSubscriptionDtoMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('subscription:read')")
    public PaginatedResult<UserSubscriptionDTO> getUserSubscriptionAsAdmin(
            UserSubscriptionFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<UserSubscription> result = userSubscriptionQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, userSubscriptionDtoMapper::toDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('subscription:read')")
    public UserSubscriptionDTO getUserSubscriptionByIdAsAdmin(@PathVariable Long id) {
        return userSubscriptionDtoMapper.toDTO(subscribeUseCase.getById(id));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('subscription:manage')")
    public UserSubscriptionDTO cancelUserSubscriptionAsAdmin(@PathVariable Long id) {
        return userSubscriptionDtoMapper.toDTO(subscribeUseCase.cancel(id, true));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('subscription:manage')")
    public void deleteUserSubscriptionAsAdmin(@PathVariable Long id) {
        UserSubscription subscription = subscribeUseCase.getById(id);
        subscribeUseCase.cancel(subscription.getId(), true);
    }
}
