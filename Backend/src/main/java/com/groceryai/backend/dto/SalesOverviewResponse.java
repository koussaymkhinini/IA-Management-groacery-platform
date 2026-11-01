package com.groceryai.backend.dto;

import java.math.BigDecimal;

public record SalesOverviewResponse(
        Long totalSales,
        Long totalQuantitySold,
        BigDecimal recordedRevenue,
        Long salesWithRecordedRevenue,
        Long salesWithoutRecordedRevenue
) {}