package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CityResponse;
import com.groceryai.backend.service.CityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<CityResponse> getAllCities() {
        return cityService.getAllCities();
    }

    // Put the specific route before the generic route
    @GetMapping("/country/{countryId}")
    public List<CityResponse> getCitiesByCountry(
            @PathVariable Integer countryId) {
        return cityService.getCitiesByCountry(countryId);
    }

    @GetMapping("/{id:\\d+}")
    public CityResponse getCityById(@PathVariable Integer id) {
        return cityService.getCityById(id);
    }
}