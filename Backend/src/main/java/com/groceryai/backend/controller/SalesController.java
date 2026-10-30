package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CreateSaleRequest;
import com.groceryai.backend.dto.SaleResponse;
import com.groceryai.backend.dto.UpdateSaleRequest;
import com.groceryai.backend.service.SaleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
public class SalesController {

    private final SaleService saleService;

    public SalesController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public List<SaleResponse> getAllSales() {
        return saleService.getAllSales();
    }

    @GetMapping("/{id:\\d+}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
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

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(
            @RequestBody CreateSaleRequest request) {

        SaleResponse response = saleService.createSale(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id:\\d+}")
    public SaleResponse updateSale(
            @PathVariable Long id,
            @RequestBody UpdateSaleRequest request) {

        return saleService.updateSale(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}