package com.maitrisetcf.infrastructure.adapter.out.query;

import com.maitrisetcf.domain.models.query.PagedResult;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@Setter
@Getter
public class PaginatedResult<T> {
    @Nonnull
    private final List<T> items;
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalItems;


    public <E> PaginatedResult(PagedResult<E> pagedResult, Function<E, T> toDTO) {
        this.items = pagedResult.items().stream().map(toDTO).toList();
        this.page = pagedResult.page();
        this.size = pagedResult.size();
        this.totalPages = pagedResult.totalPages();
        this.totalItems = pagedResult.totalItems();
    }

    /**
     * Necessary for the serialization.
     */
    protected PaginatedResult() {
        this.items = new ArrayList<>();
        this.page = 0;
        this.size = 0;
        this.totalPages = 0;
        this.totalItems = 0;
    }
}
