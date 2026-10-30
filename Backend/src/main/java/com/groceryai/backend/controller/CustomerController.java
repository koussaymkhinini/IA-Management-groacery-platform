package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CustomerResponse;
import com.groceryai.backend.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/city/{cityId}")
    public List<CustomerResponse> getCustomersByCity(
            @PathVariable Integer cityId) {
        return customerService.getCustomersByCity(cityId);
    }

    @GetMapping("/{id:\\d+}")
    public CustomerResponse getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id);
    }
}