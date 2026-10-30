package com.groceryai.backend.repository;

import com.groceryai.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {

    List<Customer> findByCity_CityId(Integer cityId);
}