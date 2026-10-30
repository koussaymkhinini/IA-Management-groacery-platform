package com.groceryai.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "city_name", nullable = false, length = 150)
    private String cityName;

    @Column(length = 20)
    private String zipcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    protected City() {
        // Required by JPA
    }

    public City(
            Integer cityId,
            String cityName,
            String zipcode,
            Country country) {

        this.cityId = cityId;
        this.cityName = cityName;
        this.zipcode = zipcode;
        this.country = country;
    }

    public Integer getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getZipcode() {
        return zipcode;
    }

    public Country getCountry() {
        return country;
    }

    public void update(
            String cityName,
            String zipcode,
            Country country) {

        this.cityName = cityName;
        this.zipcode = zipcode;
        this.country = country;
    }
}