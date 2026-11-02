package com.groceryai.backend.service;

import com.groceryai.backend.dto.AgentPageResponse;
import com.groceryai.backend.dto.CityResponse;
import com.groceryai.backend.dto.CreateCityRequest;
import com.groceryai.backend.dto.UpdateCityRequest;
import com.groceryai.backend.entity.City;
import com.groceryai.backend.entity.Country;
import com.groceryai.backend.repository.CityRepository;
import com.groceryai.backend.repository.CountryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CityService(
            CityRepository cityRepository,
            CountryRepository countryRepository) {

        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    // GET all cities
    public List<CityResponse> getAllCities() {
        return cityRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET city by ID
    public CityResponse getCityById(Integer id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + id
                ));

        return toResponse(city);
    }

    // GET cities by country
    public List<CityResponse> getCitiesByCountry(Integer countryId) {
        return cityRepository.findByCountry_CountryId(countryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // POST create city
    @Transactional
    public CityResponse createCity(CreateCityRequest request) {

        validateCityData(
                request.cityName(),
                request.zipcode()
        );

        if (request.cityId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City ID is required."
            );
        }

        if (request.countryId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Country ID is required."
            );
        }

        if (cityRepository.existsById(request.cityId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "City already exists with ID: " + request.cityId()
            );
        }

        Country country = findCountry(request.countryId());

        City city = new City(
                request.cityId(),
                request.cityName().trim(),
                normalizeZipcode(request.zipcode()),
                country
        );

        return toResponse(cityRepository.saveAndFlush(city));
    }

    // PUT update city
    @Transactional
    public CityResponse updateCity(
            Integer id,
            UpdateCityRequest request) {

        validateCityData(
                request.cityName(),
                request.zipcode()
        );

        if (request.countryId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Country ID is required."
            );
        }

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + id
                ));

        Country country = findCountry(request.countryId());

        city.update(
                request.cityName().trim(),
                normalizeZipcode(request.zipcode()),
                country
        );

        return toResponse(cityRepository.saveAndFlush(city));
    }

    // DELETE city
    @Transactional
    public void deleteCity(Integer id) {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + id
                ));

        try {
            cityRepository.delete(city);
            cityRepository.flush();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this city because customers or employees reference it."
            );
        }
    }

    // Find country or return 404
    private Country findCountry(Integer countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Country not found with ID: " + countryId
                ));
    }

    // Validate city fields
    private void validateCityData(String cityName, String zipcode) {

        if (cityName == null || cityName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City name cannot be empty."
            );
        }

        if (cityName.trim().length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City name cannot exceed 150 characters."
            );
        }

        if (zipcode != null && zipcode.length() > 20) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Zipcode cannot exceed 20 characters."
            );
        }
    }

    // Convert blank zipcode to null
    private String normalizeZipcode(String zipcode) {
        return zipcode == null || zipcode.isBlank()
                ? null
                : zipcode.trim();
    }

    // Entity to DTO conversion
    private CityResponse toResponse(City city) {
        return new CityResponse(
                city.getCityId(),
                city.getCityName(),
                city.getZipcode(),
                city.getCountry().getCountryId(),
                city.getCountry().getCountryName(),
                city.getCountry().getCountryCode()
        );
    }
    public AgentPageResponse<CityResponse> getCitiesForAgent(
        int page, int size) {

    Page<CityResponse> result = cityRepository.findAll(
            PageRequest.of(
                    Math.max(0, page),
                    Math.max(1, Math.min(size, 20)),
                    Sort.by("cityId").ascending()
            )
    ).map(this::toResponse);

    return new AgentPageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
    );
}

public AgentPageResponse<CityResponse> getCitiesByCountryForAgent(
        Integer countryId, int page, int size) {

    Page<CityResponse> result =
            cityRepository.findByCountry_CountryId(
                    countryId,
                    PageRequest.of(
                            Math.max(0, page),
                            Math.max(1, Math.min(size, 20)),
                            Sort.by("cityId").ascending()
                    )
            ).map(this::toResponse);

    return new AgentPageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
    );
}
}