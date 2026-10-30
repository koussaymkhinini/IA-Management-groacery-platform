package com.groceryai.backend.dto;

public record UpdateCityRequest(
        String cityName,
        String zipcode,
        Integer countryId
) {}