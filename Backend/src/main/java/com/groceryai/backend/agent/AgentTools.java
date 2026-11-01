package com.groceryai.backend.agent;

import com.groceryai.backend.dto.*;
import com.groceryai.backend.service.EmployeeAnalyticsService;
import com.groceryai.backend.service.SalesAnalyticsService;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentTools {

    private final SalesAnalyticsService salesAnalyticsService;
    private final EmployeeAnalyticsService employeeAnalyticsService;

    public AgentTools(
            SalesAnalyticsService salesAnalyticsService,
            EmployeeAnalyticsService employeeAnalyticsService) {

        this.salesAnalyticsService = salesAnalyticsService;
        this.employeeAnalyticsService = employeeAnalyticsService;
    }

    @Tool(description = """
            Retrieves the overall sales statistics of the grocery business.
            Returns total sales, total quantity sold, recorded revenue,
            the number of sales with recorded revenue, and the number
            without recorded revenue.
            Use this when the user asks for general sales statistics or an overview.
            """)
    public SalesOverviewResponse getSalesOverview() {

        return salesAnalyticsService.getSalesOverview();
    }

    @Tool(description = """
            Retrieves monthly sales statistics over time.
            Returns the month, number of sales, quantity sold, recorded revenue,
            and counts of sales with and without recorded revenue.
            Use this when the user asks about monthly trends or sales evolution.
            """)
    public List<SalesRevenueOverTimeResponse> getRevenueOverTime() {

        return salesAnalyticsService.getRevenueOverTime();
    }

    @Tool(description = """
            Retrieves the best-selling products ranked by total quantity sold.
            The limit must be between 1 and 20.
            Use this when the user asks about popular products or product rankings.
            """)
    public List<TopSellingProductResponse> getTopSellingProducts(
            @ToolParam(description = "Number of products to return, between 1 and 20")
            Integer limit) {

        return applyLimit(
                salesAnalyticsService.getTopSellingProducts(),
                limit
        );
    }

    @Tool(description = """
            Retrieves sales statistics grouped by product category.
            Returns category ID, category name, total quantity sold,
            and number of sales.
            Use this when the user asks which categories perform best.
            """)
    public List<SalesByCategoryResponse> getSalesByCategory() {

        return salesAnalyticsService.getSalesByCategory();
    }

    @Tool(description = """
            Retrieves the customers ranked by their number of purchases.
            Returns customer ID, customer name, number of purchases,
            and total quantity purchased.
            The limit must be between 1 and 20.
            Use this when the user asks about top customers or customer purchasing activity.
            """)
    public List<TopCustomerResponse> getTopCustomers(
            @ToolParam(description = "Number of customers to return, between 1 and 20")
            Integer limit) {

        return applyLimit(
                salesAnalyticsService.getTopCustomers(),
                limit
        );
    }

    @Tool(description = """
            Retrieves employee sales performance ranked by quantity sold.
            Returns employee ID, employee name, number of sales,
            and total quantity sold.
            The limit must be between 1 and 20.
            Use this when the user asks about employee performance.
            """)
    public List<EmployeePerformanceResponse> getEmployeePerformance(
            @ToolParam(description = "Number of employees to return, between 1 and 20")
            Integer limit) {

        return applyLimit(
                employeeAnalyticsService.getEmployeePerformance(),
                limit
        );
    }

    private <T> List<T> applyLimit(List<T> results, Integer requestedLimit) {

        int limit = requestedLimit == null
                ? 10
                : Math.max(1, Math.min(requestedLimit, 20));

        return results.stream()
                .limit(limit)
                .toList();
    }
}