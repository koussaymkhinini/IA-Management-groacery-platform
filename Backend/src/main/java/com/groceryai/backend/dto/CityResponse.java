package com.groceryai.backend.dto;

public record CityResponse(
        Integer cityId,
        String cityName,
        String zipcode,
        Integer countryId,
        String countryName,
        String countryCode
) {}