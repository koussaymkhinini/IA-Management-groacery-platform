package com.groceryai.backend.repository;

public interface EmployeePerformanceProjection {
    Integer getEmployeeId();
    String getEmployeeName();
    Long getTotalSales();
    Long getTotalQuantitySold();
}