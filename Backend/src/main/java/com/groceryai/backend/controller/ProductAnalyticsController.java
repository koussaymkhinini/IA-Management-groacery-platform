package com.groceryai.backend.controller;

import com.groceryai.backend.dto.TopSellingProductResponse;
import com.groceryai.backend.service.SalesAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/products")
public class ProductAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    public ProductAnalyticsController(
            SalesAnalyticsService salesAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
    }

    @GetMapping("/top-selling")
    public List<TopSellingProductResponse> getTopSellingProducts() {
        return salesAnalyticsService.getTopSellingProducts();
    }
}