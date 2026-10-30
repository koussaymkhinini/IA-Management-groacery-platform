package com.groceryai.backend.dto;

import java.time.LocalDateTime;

public record EmployeeResponse(
        Integer employeeId,
        String firstName,
        String middleInitial,
        String lastName,
        LocalDateTime birthDate,
        String gender,
        Integer cityId,
        String cityName,
        LocalDateTime hireDate
) {}