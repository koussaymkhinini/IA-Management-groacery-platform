
package com.groceryai.backend.dto;

import java.util.List;

public record AgentPageResponse<T>(
        List<T> records,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {}
