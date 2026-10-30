package com.groceryai.backend.dto;

public record CreateCountryRequest(
        Integer countryId,
        String countryName,
        String countryCode
) {}