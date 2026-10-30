package com.groceryai.backend.dto;

import java.time.LocalDateTime;

public record CreateEmployeeRequest(
        Integer employeeId,
        String firstName,
        String middleInitial,
        String lastName,
        LocalDateTime birthDate,
        String gender,
        Integer cityId,
        LocalDateTime hireDate
) {}