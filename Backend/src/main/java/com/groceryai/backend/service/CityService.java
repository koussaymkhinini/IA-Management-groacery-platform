package com.groceryai.backend.service;

import com.groceryai.backend.dto.CityResponse;
import com.groceryai.backend.entity.City;
import com.groceryai.backend.entity.Country;
import com.groceryai.backend.repository.CityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityResponse> getAllCities() {
        return cityRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CityResponse getCityById(Integer id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + id
                ));

        return toResponse(city);
    }

    public List<CityResponse> getCitiesByCountry(Integer countryId) {
        return cityRepository.findByCountry_CountryId(countryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CityResponse toResponse(City city) {
        Country country = city.getCountry();

        return new CityResponse(
                city.getCityId(),
                city.getCityName(),
                city.getZipcode(),
                country.getCountryId(),
                country.getCountryName(),
                country.getCountryCode()
        );
    }
}