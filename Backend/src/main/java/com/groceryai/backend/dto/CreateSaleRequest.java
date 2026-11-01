package com.groceryai.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateSaleRequest(
        Long salesId,
        Integer salespersonId,
        Integer customerId,
        Integer productId,
        Integer quantity,
        BigDecimal discount,
        LocalDateTime salesDate,
        String transactionNumber
) {}