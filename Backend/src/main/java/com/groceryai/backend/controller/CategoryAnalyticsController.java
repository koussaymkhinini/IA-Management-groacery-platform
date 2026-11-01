package com.groceryai.backend.controller;

import com.groceryai.backend.dto.SalesByCategoryResponse;
import com.groceryai.backend.service.SalesAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/categories")
public class CategoryAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    public CategoryAnalyticsController(
            SalesAnalyticsService salesAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
    }

    @GetMapping("/sales")
    public List<SalesByCategoryResponse> getSalesByCategory() {
        return salesAnalyticsService.getSalesByCategory();
    }
}