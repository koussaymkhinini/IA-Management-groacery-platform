package com.groceryai.backend.dto;

public record CreateCityRequest(
        Integer cityId,
        String cityName,
        String zipcode,
        Integer countryId
) {}