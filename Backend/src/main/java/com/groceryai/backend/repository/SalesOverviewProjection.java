package com.groceryai.backend.repository;

import java.math.BigDecimal;

public interface SalesOverviewProjection {

    Long getTotalSales();

    Long getTotalQuantitySold();

    BigDecimal getRecordedRevenue();

    Long getSalesWithRecordedRevenue();

    Long getSalesWithoutRecordedRevenue();
}