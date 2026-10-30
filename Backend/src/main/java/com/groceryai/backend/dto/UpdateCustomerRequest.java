package com.groceryai.backend.dto;

public record UpdateCustomerRequest(
        String firstName,
        String middleInitial,
        String lastName,
        Integer cityId,
        String address
) {}