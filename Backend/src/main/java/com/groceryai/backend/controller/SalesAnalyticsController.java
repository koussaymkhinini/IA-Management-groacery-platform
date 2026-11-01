package com.groceryai.backend.controller;

import com.groceryai.backend.dto.SalesOverviewResponse;
import com.groceryai.backend.dto.SalesRevenueOverTimeResponse;
import com.groceryai.backend.service.SalesAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/sales")
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    public SalesAnalyticsController(SalesAnalyticsService salesAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
    }

    @GetMapping("/overview")
    public SalesOverviewResponse getSalesOverview() {
        return salesAnalyticsService.getSalesOverview();
    }

    @GetMapping("/revenue-over-time")
    public List<SalesRevenueOverTimeResponse> getRevenueOverTime() {
        return salesAnalyticsService.getRevenueOverTime();
    }
}