package com.groceryai.backend.service;

import com.groceryai.backend.dto.AgentPageResponse;
import com.groceryai.backend.dto.CreateEmployeeRequest;
import com.groceryai.backend.dto.EmployeeResponse;
import com.groceryai.backend.dto.UpdateEmployeeRequest;
import com.groceryai.backend.entity.City;
import com.groceryai.backend.entity.Employee;
import com.groceryai.backend.repository.CityRepository;
import com.groceryai.backend.repository.EmployeeRepository;
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
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CityRepository cityRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           CityRepository cityRepository) {
        this.employeeRepository = employeeRepository;
        this.cityRepository = cityRepository;
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
                        "Employee not found with ID: " + id));

        return toResponse(employee);
    }

    public List<EmployeeResponse> getEmployeesByCity(Integer cityId) {
        return employeeRepository.findByCity_CityId(cityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        validateEmployeeData(
                request.firstName(),
                request.middleInitial(),
                request.lastName(),
                request.gender()
        );

        if (request.employeeId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Employee ID is required.");
        }

        if (request.cityId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "City ID is required.");
        }

        if (employeeRepository.existsById(request.employeeId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Employee already exists with ID: " + request.employeeId());
        }

        City city = findCity(request.cityId());

        Employee employee = new Employee(
                request.employeeId(),
                request.firstName().trim(),
                normalizeOptional(request.middleInitial()),
                request.lastName().trim(),
                request.birthDate(),
                normalizeOptional(request.gender()),
                city,
                request.hireDate()
        );

        return toResponse(employeeRepository.saveAndFlush(employee));
    }

    @Transactional
    public EmployeeResponse updateEmployee(
            Integer id, UpdateEmployeeRequest request) {

        validateEmployeeData(
                request.firstName(),
                request.middleInitial(),
                request.lastName(),
                request.gender()
        );

        if (request.cityId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "City ID is required.");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found with ID: " + id));

        City city = findCity(request.cityId());

        employee.update(
                request.firstName().trim(),
                normalizeOptional(request.middleInitial()),
                request.lastName().trim(),
                request.birthDate(),
                normalizeOptional(request.gender()),
                city,
                request.hireDate()
        );

        return toResponse(employeeRepository.saveAndFlush(employee));
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found with ID: " + id));

        try {
            employeeRepository.delete(employee);
            employeeRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this employee because sales reference them.");
        }
    }

    private City findCity(Integer cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + cityId));
    }

    private void validateEmployeeData(
            String firstName,
            String middleInitial,
            String lastName,
            String gender) {

        if (firstName == null || firstName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "First name cannot be empty.");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Last name cannot be empty.");
        }

        if (firstName.trim().length() > 100 ||
                lastName.trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "First name and last name cannot exceed 100 characters.");
        }

        if (middleInitial != null && middleInitial.trim().length() > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Middle initial cannot exceed 10 characters.");
        }

        if (gender != null && gender.trim().length() > 20) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Gender cannot exceed 20 characters.");
        }
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getMiddleInitial(),
                employee.getLastName(),
                employee.getBirthDate(),
                employee.getGender(),
                employee.getCity().getCityId(),
                employee.getCity().getCityName(),
                employee.getHireDate()
        );
    }
    public AgentPageResponse<EmployeeResponse> getEmployeesForAgent(
        int page, int size) {

    Page<EmployeeResponse> result = employeeRepository.findAll(
            PageRequest.of(
                    Math.max(0, page),
                    Math.max(1, Math.min(size, 20)),
                    Sort.by("employeeId").ascending()
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

public AgentPageResponse<EmployeeResponse> getEmployeesByCityForAgent(
        Integer cityId, int page, int size) {

    Page<EmployeeResponse> result =
            employeeRepository.findByCity_CityId(
                    cityId,
                    PageRequest.of(
                            Math.max(0, page),
                            Math.max(1, Math.min(size, 20)),
                            Sort.by("employeeId").ascending()
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