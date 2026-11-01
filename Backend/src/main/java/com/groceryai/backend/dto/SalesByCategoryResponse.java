package com.groceryai.backend.dto;

public record SalesByCategoryResponse(
        Integer categoryId,
        String categoryName,
        Long totalQuantitySold,
        Long totalSales
) {}