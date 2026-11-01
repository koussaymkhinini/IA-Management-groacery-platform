package com.groceryai.backend.controller;

import com.groceryai.backend.dto.EmployeePerformanceResponse;
import com.groceryai.backend.service.EmployeeAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/employees")
public class EmployeeAnalyticsController {

    private final EmployeeAnalyticsService employeeAnalyticsService;

    public EmployeeAnalyticsController(
            EmployeeAnalyticsService employeeAnalyticsService) {
        this.employeeAnalyticsService = employeeAnalyticsService;
    }

    @GetMapping("/performance")
    public List<EmployeePerformanceResponse> getEmployeePerformance() {
        return employeeAnalyticsService.getEmployeePerformance();
    }
}