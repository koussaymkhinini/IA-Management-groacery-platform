package com.groceryai.backend.dto;

public record TopSellingProductResponse(
        Integer productId,
        String productName,
        Long totalQuantitySold,
        Long totalSales
) {}