package com.groceryai.backend.repository;

import com.groceryai.backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {

    List<City> findByCountry_CountryId(Integer countryId);
}