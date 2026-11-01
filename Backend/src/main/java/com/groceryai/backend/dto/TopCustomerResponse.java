package com.groceryai.backend.dto;

public record TopCustomerResponse(
        Integer customerId,
        String customerName,
        Long totalPurchases,
        Long totalQuantityPurchased
) {}