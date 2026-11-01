package com.groceryai.backend.service;

import com.groceryai.backend.dto.SalesOverviewResponse;
import com.groceryai.backend.dto.SalesRevenueOverTimeResponse;
import com.groceryai.backend.repository.SaleRepository;
import com.groceryai.backend.repository.SalesOverviewProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SalesAnalyticsService {

    private final SaleRepository saleRepository;

    public SalesAnalyticsService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
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