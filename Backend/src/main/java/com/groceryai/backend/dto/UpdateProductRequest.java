package com.groceryai.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateProductRequest(
        String productName,
        BigDecimal price,
        Integer categoryId,
        String productClass,
        LocalDateTime modifyDate,
        String resistant,
        String isAllergic,
        BigDecimal vitalityDays
) {}