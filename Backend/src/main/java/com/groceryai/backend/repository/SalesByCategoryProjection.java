package com.groceryai.backend.repository;

public interface SalesByCategoryProjection {

    Integer getCategoryId();

    String getCategoryName();

    Long getTotalQuantitySold();

    Long getTotalSales();
}