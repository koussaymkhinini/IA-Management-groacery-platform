package com.groceryai.backend.controller;

import com.groceryai.backend.dto.SaleResponse;
import com.groceryai.backend.service.SaleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public List<SaleResponse> getAllSales() {
        return saleService.getAllSales();
    }

    @GetMapping("/customer/{customerId}")
    public List<SaleResponse> getSalesByCustomer(
            @PathVariable Integer customerId) {
        return saleService.getSalesByCustomer(customerId);
    }

    @GetMapping("/employee/{employeeId}")
    public List<SaleResponse> getSalesByEmployee(
            @PathVariable Integer employeeId) {
        return saleService.getSalesByEmployee(employeeId);
    }

    @GetMapping("/product/{productId}")
    public List<SaleResponse> getSalesByProduct(
            @PathVariable Integer productId) {
        return saleService.getSalesByProduct(productId);
    }

    @GetMapping("/{id:\\d+}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }
}