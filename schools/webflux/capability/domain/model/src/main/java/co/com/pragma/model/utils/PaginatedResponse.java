package co.com.pragma.model.utils;

import lombok.Builder;

import java.util.List;


@Builder
public record PaginatedResponse<T>(
        Long totalRecords,
        Integer size,
        Integer page,
        Long totalPages,
        List<T> items
) {
}
