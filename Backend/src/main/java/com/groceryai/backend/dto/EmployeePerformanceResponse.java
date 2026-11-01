package com.groceryai.backend.dto;

public record EmployeePerformanceResponse(
        Integer employeeId,
        String employeeName,
        Long totalSales,
        Long totalQuantitySold
) {}