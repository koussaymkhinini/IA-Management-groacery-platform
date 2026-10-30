package com.groceryai.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_initial", length = 10)
    private String middleInitial;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Column(length = 20)
    private String gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    protected Employee() {}

    public Integer getEmployeeId() {
        return employeeId;
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

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public City getCity() {
        return city;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }
}