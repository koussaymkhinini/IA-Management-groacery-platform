package com.groceryai.backend.repository;

import com.groceryai.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeRepository
        extends JpaRepository<Employee, Integer> {

    List<Employee> findByCity_CityId(Integer cityId);
    Page<Employee> findByCity_CityId(
        Integer cityId,
        Pageable pageable
);
}