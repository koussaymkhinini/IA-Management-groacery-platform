package com.groceryai.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponse(
        Long salesId,

        Integer salespersonId,
        String salespersonName,

        Integer customerId,
        String customerName,

        Integer productId,
        String productName,

        Integer quantity,
        BigDecimal discount,
        BigDecimal totalPrice,
        LocalDateTime salesDate,
        String transactionNumber
) {}