package com.groceryai.backend.repository;

public interface TopCustomerProjection {

    Integer getCustomerId();

    String getCustomerName();

    Long getTotalPurchases();

    Long getTotalQuantityPurchased();
}