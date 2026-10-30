package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CreateEmployeeRequest;
import com.groceryai.backend.dto.EmployeeResponse;
import com.groceryai.backend.dto.UpdateEmployeeRequest;
import com.groceryai.backend.service.EmployeeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id:\\d+}")
    public EmployeeResponse getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/city/{cityId}")
    public List<EmployeeResponse> getEmployeesByCity(
            @PathVariable Integer cityId) {
        return employeeService.getEmployeesByCity(cityId);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestBody CreateEmployeeRequest request) {

        EmployeeResponse response = employeeService.createEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id:\\d+}")
    public EmployeeResponse updateEmployee(
            @PathVariable Integer id,
            @RequestBody UpdateEmployeeRequest request) {

        return employeeService.updateEmployee(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}