package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.models.discountcode.DiscountCode;
import com.hadiasaas.domain.models.discountcode.DiscountCodeFilter;
import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.ports.in.DiscountCodeQueryUseCase;
import com.hadiasaas.domain.ports.in.DiscountCodeUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.DiscountCodeDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.DiscountCodeDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateDiscountCodeRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateDiscountCodeRequest;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
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

import static com.hadiasaas.util.PaginationConstants.DEFAULT_PAGE_SIZE_INT;

/**
 * REST controller for admin discount code management.
 */
@Validated
@RestController
@Tag(name = "Admin discount code management")
@RequestMapping("/api/v1/admin/discount-codes")
@RequiredArgsConstructor
public class AdminDiscountCodeController {

    private final DiscountCodeUseCase discountCodeUseCase;
    private final DiscountCodeQueryUseCase discountCodeQueryUseCase;
    private final DiscountCodeDtoMapper discountCodeDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('discount-code:create')")
    public DiscountCodeDTO createDiscountCodeAsAdmin(@Valid @RequestBody CreateDiscountCodeRequest request) {
        return discountCodeDtoMapper.toDTO(discountCodeUseCase.create(
                request.code(),
                request.discountType(),
                request.discountValue(),
                request.currencyCode(),
                Boolean.TRUE.equals(request.active()),
                request.expirationDate(),
                request.maxUsages()
        ));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('discount-code:read')")
    public PaginatedResult<DiscountCodeDTO> getDiscountCodesAsAdmin(
            DiscountCodeFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<DiscountCode> result = discountCodeQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, discountCodeDtoMapper::toDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('discount-code:read')")
    public DiscountCodeDTO getDiscountCodesByIdAsAdmin(@PathVariable Long id) {
        return discountCodeDtoMapper.toDTO(discountCodeUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('discount-code:update')")
    public DiscountCodeDTO updateDiscountCodeAsAdmin(@PathVariable Long id, @Valid @RequestBody UpdateDiscountCodeRequest request) {
        return discountCodeDtoMapper.toDTO(discountCodeUseCase.update(
                id,
                request.code(),
                request.discountType(),
                request.discountValue(),
                request.currencyCode(),
                Boolean.TRUE.equals(request.active()),
                request.expirationDate(),
                request.maxUsages()
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('discount-code:delete')")
    public void deleteDiscountCodeAsAdmin(@PathVariable Long id) {
        discountCodeUseCase.delete(id);
    }
}
