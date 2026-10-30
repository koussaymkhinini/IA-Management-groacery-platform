package com.groceryai.backend.dto;

import java.time.LocalDateTime;

public record UpdateEmployeeRequest(
        String firstName,
        String middleInitial,
        String lastName,
        LocalDateTime birthDate,
        String gender,
        Integer cityId,
        LocalDateTime hireDate
) {}