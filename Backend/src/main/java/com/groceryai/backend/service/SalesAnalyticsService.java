package com.groceryai.backend.service;

import com.groceryai.backend.dto.EmployeePerformanceResponse;
import com.groceryai.backend.dto.SalesByCategoryResponse;
import com.groceryai.backend.dto.SalesOverviewResponse;
import com.groceryai.backend.dto.SalesRevenueOverTimeResponse;
import com.groceryai.backend.dto.TopCustomerResponse;
import com.groceryai.backend.repository.SaleRepository;
import com.groceryai.backend.repository.SalesOverviewProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.groceryai.backend.dto.TopSellingProductResponse;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SalesAnalyticsService {

    private final SaleRepository saleRepository;

    public SalesAnalyticsService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public List<TopCustomerResponse> getTopCustomers() {

    return saleRepository.getTopCustomers()
            .stream()
            .map(result -> new TopCustomerResponse(
                    result.getCustomerId(),
                    result.getCustomerName(),
                    result.getTotalPurchases(),
                    result.getTotalQuantityPurchased()
            ))
            .toList();
        }
        public List<EmployeePerformanceResponse> getEmployeePerformance() {
    return saleRepository.getEmployeePerformance()
            .stream()
            .map(result -> new EmployeePerformanceResponse(
                    result.getEmployeeId(),
                    result.getEmployeeName(),
                    result.getTotalSales(),
                    result.getTotalQuantitySold()
            ))
            .toList();
        }

    public List<SalesByCategoryResponse> getSalesByCategory() {

    return saleRepository.getSalesByCategory()
            .stream()
            .map(result -> new SalesByCategoryResponse(
                    result.getCategoryId(),
                    result.getCategoryName(),
                    result.getTotalQuantitySold(),
                    result.getTotalSales()
            ))
            .toList();
}

    public List<TopSellingProductResponse> getTopSellingProducts() {

    return saleRepository.getTopSellingProducts()
            .stream()
            .map(result -> new TopSellingProductResponse(
                    result.getProductId(),
                    result.getProductName(),
                    result.getTotalQuantitySold(),
                    result.getTotalSales()
            ))
            .toList();
        }

    public SalesOverviewResponse getSalesOverview() {

        SalesOverviewProjection result = saleRepository.getSalesOverview();

        Long totalSales = result.getTotalSales() == null
                ? 0L : result.getTotalSales();

        Long totalQuantitySold = result.getTotalQuantitySold() == null
                ? 0L : result.getTotalQuantitySold();

        BigDecimal recordedRevenue = result.getRecordedRevenue() == null
                ? BigDecimal.ZERO : result.getRecordedRevenue();

        Long salesWithRecordedRevenue =
                result.getSalesWithRecordedRevenue() == null
                        ? 0L : result.getSalesWithRecordedRevenue();

        Long salesWithoutRecordedRevenue =
                result.getSalesWithoutRecordedRevenue() == null
                        ? 0L : result.getSalesWithoutRecordedRevenue();

        return new SalesOverviewResponse(
                totalSales,
                totalQuantitySold,
                recordedRevenue,
                salesWithRecordedRevenue,
                salesWithoutRecordedRevenue
        );
    }

    public List<SalesRevenueOverTimeResponse> getRevenueOverTime() {

        return saleRepository.getRevenueOverTime()
                .stream()
                .map(result -> new SalesRevenueOverTimeResponse(
                        result.getMonth(),
                        result.getTotalSales(),
                        result.getTotalQuantitySold(),
                        result.getRecordedRevenue(),
                        result.getSalesWithRecordedRevenue(),
                        result.getSalesWithoutRecordedRevenue()
                ))
                .toList();
    }
}