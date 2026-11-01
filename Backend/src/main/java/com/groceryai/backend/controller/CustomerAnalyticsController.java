package com.groceryai.backend.controller;

import com.groceryai.backend.dto.TopCustomerResponse;
import com.groceryai.backend.service.SalesAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/customers")
public class CustomerAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    public CustomerAnalyticsController(
            SalesAnalyticsService salesAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
    }

    @GetMapping("/top")
    public List<TopCustomerResponse> getTopCustomers() {
        return salesAnalyticsService.getTopCustomers();
    }
}