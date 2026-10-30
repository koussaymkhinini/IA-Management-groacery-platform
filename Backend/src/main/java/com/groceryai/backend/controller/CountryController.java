package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CountryResponse;
import com.groceryai.backend.dto.CreateCountryRequest;
import com.groceryai.backend.dto.UpdateCountryRequest;
import com.groceryai.backend.service.CountryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    // GET all countries
    @GetMapping
    public List<CountryResponse> getAllCountries() {
        return countryService.getAllCountries();
    }

    // GET country by ID
    @GetMapping("/{id:\\d+}")
    public CountryResponse getCountryById(@PathVariable Integer id) {
        return countryService.getCountryById(id);
    }

    // POST create country
    @PostMapping
    public ResponseEntity<CountryResponse> createCountry(
            @RequestBody CreateCountryRequest request) {

        CountryResponse response = countryService.createCountry(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUT update country
    @PutMapping("/{id:\\d+}")
    public CountryResponse updateCountry(
            @PathVariable Integer id,
            @RequestBody UpdateCountryRequest request) {

        return countryService.updateCountry(id, request);
    }

    // DELETE country
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer id) {

        countryService.deleteCountry(id);

        return ResponseEntity.noContent().build();
    }
}