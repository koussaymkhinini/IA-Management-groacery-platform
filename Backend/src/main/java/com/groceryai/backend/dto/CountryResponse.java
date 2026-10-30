package com.groceryai.backend.dto;

public record CountryResponse(
        Integer countryId,
        String countryName,
        String countryCode
) {}