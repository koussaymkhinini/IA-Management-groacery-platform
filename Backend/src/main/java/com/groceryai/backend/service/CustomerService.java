package com.groceryai.backend.service;

import com.groceryai.backend.dto.AgentPageResponse;
import com.groceryai.backend.dto.CreateCustomerRequest;
import com.groceryai.backend.dto.CustomerResponse;
import com.groceryai.backend.dto.UpdateCustomerRequest;
import com.groceryai.backend.entity.City;
import com.groceryai.backend.entity.Customer;
import com.groceryai.backend.repository.CityRepository;
import com.groceryai.backend.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            CityRepository cityRepository) {

        this.customerRepository = customerRepository;
        this.cityRepository = cityRepository;
    }

    // GET all customers
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET customer by ID
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found with ID: " + id
                ));

        return toResponse(customer);
    }

    // GET customers by city
    public List<CustomerResponse> getCustomersByCity(Integer cityId) {
        return customerRepository.findByCity_CityId(cityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // POST create customer
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        validateCustomerData(
                request.firstName(),
                request.middleInitial(),
                request.lastName(),
                request.address()
        );

        if (request.customerId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer ID is required."
            );
        }

        if (request.cityId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City ID is required."
            );
        }

        if (customerRepository.existsById(request.customerId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Customer already exists with ID: " + request.customerId()
            );
        }

        City city = findCity(request.cityId());

        Customer customer = new Customer(
                request.customerId(),
                request.firstName().trim(),
                normalizeOptional(request.middleInitial()),
                request.lastName().trim(),
                city,
                normalizeOptional(request.address())
        );

        return toResponse(customerRepository.saveAndFlush(customer));
    }

    // PUT update customer
    @Transactional
    public CustomerResponse updateCustomer(
            Integer id,
            UpdateCustomerRequest request) {

        validateCustomerData(
                request.firstName(),
                request.middleInitial(),
                request.lastName(),
                request.address()
        );

        if (request.cityId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City ID is required."
            );
        }

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found with ID: " + id
                ));

        City city = findCity(request.cityId());

        customer.update(
                request.firstName().trim(),
                normalizeOptional(request.middleInitial()),
                request.lastName().trim(),
                city,
                normalizeOptional(request.address())
        );

        return toResponse(customerRepository.saveAndFlush(customer));
    }

    // DELETE customer
    @Transactional
    public void deleteCustomer(Integer id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found with ID: " + id
                ));

        try {
            customerRepository.delete(customer);
            customerRepository.flush();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this customer because sales reference it."
            );
        }
    }

    // Find city or return 404
    private City findCity(Integer cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + cityId
                ));
    }

    // Validate customer fields
    private void validateCustomerData(
            String firstName,
            String middleInitial,
            String lastName,
            String address) {

        if (firstName == null || firstName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "First name cannot be empty."
            );
        }

        if (lastName == null || lastName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Last name cannot be empty."
            );
        }

        if (firstName.trim().length() > 100 ||
                lastName.trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "First name and last name cannot exceed 100 characters."
            );
        }

        if (middleInitial != null && middleInitial.length() > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Middle initial cannot exceed 10 characters."
            );
        }

        if (address != null && address.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Address cannot exceed 255 characters."
            );
        }
    }

    // Convert blank optional values to null
    private String normalizeOptional(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    // Entity to DTO conversion
    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getMiddleInitial(),
                customer.getLastName(),
                customer.getCity().getCityId(),
                customer.getCity().getCityName(),
                customer.getAddress()
        );
    }
    public AgentPageResponse<CustomerResponse> getCustomersForAgent(
        int page, int size) {

    Page<CustomerResponse> result = customerRepository.findAll(
            PageRequest.of(
                    Math.max(0, page),
                    Math.max(1, Math.min(size, 20)),
                    Sort.by("customerId").ascending()
            )
    ).map(this::toResponse);

    return new AgentPageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
    );
}

public AgentPageResponse<CustomerResponse> getCustomersByCityForAgent(
        Integer cityId, int page, int size) {

    Page<CustomerResponse> result =
            customerRepository.findByCity_CityId(
                    cityId,
                    PageRequest.of(
                            Math.max(0, page),
                            Math.max(1, Math.min(size, 20)),
                            Sort.by("customerId").ascending()
                    )
            ).map(this::toResponse);

    return new AgentPageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
    );
}
}