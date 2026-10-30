package com.groceryai.backend.service;

import com.groceryai.backend.dto.SaleResponse;
import com.groceryai.backend.entity.Sale;
import com.groceryai.backend.repository.SaleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SaleService {

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sale not found with ID: " + id
                ));

        return toResponse(sale);
    }

    public List<SaleResponse> getSalesByCustomer(Integer customerId) {
        return saleRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SaleResponse> getSalesByEmployee(Integer employeeId) {
        return saleRepository.findBySalesperson_EmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SaleResponse> getSalesByProduct(Integer productId) {
        return saleRepository.findByProduct_ProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SaleResponse toResponse(Sale sale) {

        String salespersonName =
                sale.getSalesperson().getFirstName() + " " +
                sale.getSalesperson().getLastName();

        String customerName =
                sale.getCustomer().getFirstName() + " " +
                sale.getCustomer().getLastName();

        return new SaleResponse(
                sale.getSalesId(),

                sale.getSalesperson().getEmployeeId(),
                salespersonName,

                sale.getCustomer().getCustomerId(),
                customerName,

                sale.getProduct().getProductId(),
                sale.getProduct().getProductName(),

                sale.getQuantity(),
                sale.getDiscount(),
                sale.getTotalPrice(),
                sale.getSalesDate(),
                sale.getTransactionNumber()
        );
    }
}