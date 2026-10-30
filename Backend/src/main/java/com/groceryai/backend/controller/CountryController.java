package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CountryResponse;
import com.groceryai.backend.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public List<CountryResponse> getAllCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("/{id}")
    public CountryResponse getCountryById(@PathVariable Integer id) {
        return countryService.getCountryById(id);
    }
}