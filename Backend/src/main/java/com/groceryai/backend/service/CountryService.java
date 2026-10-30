package com.groceryai.backend.service;

import com.groceryai.backend.dto.CountryResponse;
import com.groceryai.backend.entity.Country;
import com.groceryai.backend.repository.CountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CountryResponse getCountryById(Integer id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Country not found with ID: " + id
                ));

        return toResponse(country);
    }

    private CountryResponse toResponse(Country country) {
        return new CountryResponse(
                country.getCountryId(),
                country.getCountryName(),
                country.getCountryCode()
        );
    }
}