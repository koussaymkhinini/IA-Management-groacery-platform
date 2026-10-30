package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CreateCustomerRequest;
import com.groceryai.backend.dto.CustomerResponse;
import com.groceryai.backend.dto.UpdateCustomerRequest;
import com.groceryai.backend.service.CustomerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET all customers
    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // GET customer by ID
    @GetMapping("/{id:\\d+}")
    public CustomerResponse getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id);
    }

    // GET customers by city
    @GetMapping("/city/{cityId}")
    public List<CustomerResponse> getCustomersByCity(
            @PathVariable Integer cityId) {

        return customerService.getCustomersByCity(cityId);
    }

    // POST create customer
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @RequestBody CreateCustomerRequest request) {

        CustomerResponse response = customerService.createCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUT update customer
    @PutMapping("/{id:\\d+}")
    public CustomerResponse updateCustomer(
            @PathVariable Integer id,
            @RequestBody UpdateCustomerRequest request) {

        return customerService.updateCustomer(id, request);
    }

    // DELETE customer
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }
}