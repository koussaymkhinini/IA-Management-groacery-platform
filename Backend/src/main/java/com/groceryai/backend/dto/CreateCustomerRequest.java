package com.groceryai.backend.dto;

public record CreateCustomerRequest(
        Integer customerId,
        String firstName,
        String middleInitial,
        String lastName,
        Integer cityId,
        String address
) {}