package com.groceryai.backend.dto;

public record CustomerResponse(
        Integer customerId,
        String firstName,
        String middleInitial,
        String lastName,
        Integer cityId,
        String cityName,
        String address
) {}