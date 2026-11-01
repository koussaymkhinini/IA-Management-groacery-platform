package com.groceryai.backend.repository;

public interface TopSellingProductProjection {

    Integer getProductId();

    String getProductName();

    Long getTotalQuantitySold();

    Long getTotalSales();
}