package com.groceryai.backend.service;

import com.groceryai.backend.dto.EmployeeResponse;
import com.groceryai.backend.entity.City;
import com.groceryai.backend.entity.Employee;
import com.groceryai.backend.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found with ID: " + id
                ));

        return toResponse(employee);
    }

    public List<EmployeeResponse> getEmployeesByCity(Integer cityId) {
        return employeeRepository.findByCity_CityId(cityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private EmployeeResponse toResponse(Employee employee) {
        City city = employee.getCity();

        return new EmployeeResponse(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getMiddleInitial(),
                employee.getLastName(),
                employee.getBirthDate(),
                employee.getGender(),
                city.getCityId(),
                city.getCityName(),
                employee.getHireDate()
        );
    }
}