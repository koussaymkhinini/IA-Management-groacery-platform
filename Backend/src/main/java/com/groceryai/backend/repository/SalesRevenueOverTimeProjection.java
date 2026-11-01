package com.groceryai.backend.repository;

import java.math.BigDecimal;

public interface SalesRevenueOverTimeProjection {

    String getMonth();

    Long getTotalSales();

    Long getTotalQuantitySold();

    BigDecimal getRecordedRevenue();

    Long getSalesWithRecordedRevenue();

    Long getSalesWithoutRecordedRevenue();
}