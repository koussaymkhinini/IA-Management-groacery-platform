package com.groceryai.backend.service;

import com.groceryai.backend.dto.CustomerResponse;
import com.groceryai.backend.entity.City;
import com.groceryai.backend.entity.Customer;
import com.groceryai.backend.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found with ID: " + id
                ));

        return toResponse(customer);
    }

    public List<CustomerResponse> getCustomersByCity(Integer cityId) {
        return customerRepository.findByCity_CityId(cityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CustomerResponse toResponse(Customer customer) {
        City city = customer.getCity();

        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getMiddleInitial(),
                customer.getLastName(),
                city.getCityId(),
                city.getCityName(),
                customer.getAddress()
        );
    }
}