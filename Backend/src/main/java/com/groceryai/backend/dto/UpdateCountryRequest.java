package com.groceryai.backend.dto;

public record UpdateCountryRequest(
        String countryName,
        String countryCode
) {}