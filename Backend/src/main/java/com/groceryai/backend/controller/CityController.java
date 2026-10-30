package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CityResponse;
import com.groceryai.backend.dto.CreateCityRequest;
import com.groceryai.backend.dto.UpdateCityRequest;
import com.groceryai.backend.service.CityService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    // GET all cities
    @GetMapping
    public List<CityResponse> getAllCities() {
        return cityService.getAllCities();
    }

    // GET city by ID
    @GetMapping("/{id:\\d+}")
    public CityResponse getCityById(@PathVariable Integer id) {
        return cityService.getCityById(id);
    }

    // GET cities by country
    @GetMapping("/country/{countryId}")
    public List<CityResponse> getCitiesByCountry(
            @PathVariable Integer countryId) {

        return cityService.getCitiesByCountry(countryId);
    }

    // POST create city
    @PostMapping
    public ResponseEntity<CityResponse> createCity(
            @RequestBody CreateCityRequest request) {

        CityResponse response = cityService.createCity(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUT update city
    @PutMapping("/{id:\\d+}")
    public CityResponse updateCity(
            @PathVariable Integer id,
            @RequestBody UpdateCityRequest request) {

        return cityService.updateCity(id, request);
    }

    // DELETE city
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {

        cityService.deleteCity(id);

        return ResponseEntity.noContent().build();
    }
}