package com.maitrisetcf.domain.models.query;

import java.util.List;

public record PagedResult<T>(
        List<T> items,
        long totalItems,
        int page,
        int size,
        int totalPages
) {}
