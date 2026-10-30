package com.groceryai.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    protected Country() {}

    public Integer getCountryId() {
        return countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}