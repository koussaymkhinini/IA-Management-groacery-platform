package com.groceryai.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_initial", length = 10)
    private String middleInitial;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(length = 255)
    private String address;

    protected Customer() {
        // Required by JPA
    }

    public Customer(
            Integer customerId,
            String firstName,
            String middleInitial,
            String lastName,
            City city,
            String address) {

        this.customerId = customerId;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.city = city;
        this.address = address;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public City getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public void update(
            String firstName,
            String middleInitial,
            String lastName,
            City city,
            String address) {

        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.city = city;
        this.address = address;
    }
}