package com.groceryai.backend.service;

import com.groceryai.backend.dto.CountryResponse;
import com.groceryai.backend.dto.CreateCountryRequest;
import com.groceryai.backend.dto.UpdateCountryRequest;
import com.groceryai.backend.entity.Country;
import com.groceryai.backend.repository.CountryRepository;

import org.springframework.dao.DataIntegrityViolationException;
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

    // GET all countries
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET country by ID
    public CountryResponse getCountryById(Integer id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Country not found with ID: " + id
                ));

        return toResponse(country);
    }

    // POST create country
    @Transactional
    public CountryResponse createCountry(CreateCountryRequest request) {

        validateCountryData(
                request.countryName(),
                request.countryCode()
        );

        if (request.countryId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Country ID is required."
            );
        }

        if (countryRepository.existsById(request.countryId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Country already exists with ID: " + request.countryId()
            );
        }

        Country country = new Country(
                request.countryId(),
                request.countryName().trim(),
                request.countryCode().trim()
        );

        return toResponse(countryRepository.saveAndFlush(country));
    }

    // PUT update country
    @Transactional
    public CountryResponse updateCountry(
            Integer id,
            UpdateCountryRequest request) {

        validateCountryData(
                request.countryName(),
                request.countryCode()
        );

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Country not found with ID: " + id
                ));

        country.update(
                request.countryName().trim(),
                request.countryCode().trim()
        );

        return toResponse(countryRepository.saveAndFlush(country));
    }

    // DELETE country
    @Transactional
    public void deleteCountry(Integer id) {

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Country not found with ID: " + id
                ));

        try {
            countryRepository.delete(country);
            countryRepository.flush();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this country because cities reference it."
            );
        }
    }

    // Validate required fields
    private void validateCountryData(String countryName, String countryCode) {

        if (countryName == null || countryName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Country name cannot be empty."
            );
        }

        if (countryCode == null || countryCode.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Country code cannot be empty."
            );
        }

        if (countryName.trim().length() > 100 ||
                countryCode.trim().length() > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Country name or code exceeds the allowed length."
            );
        }
    }

    // Entity to DTO conversion
    private CountryResponse toResponse(Country country) {
        return new CountryResponse(
                country.getCountryId(),
                country.getCountryName(),
                country.getCountryCode()
        );
    }
}