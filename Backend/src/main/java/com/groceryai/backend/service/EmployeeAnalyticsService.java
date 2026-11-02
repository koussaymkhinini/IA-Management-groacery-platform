package com.groceryai.backend.service;

import com.groceryai.backend.dto.EmployeePerformanceResponse;
import com.groceryai.backend.repository.SaleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeAnalyticsService {

    private final SaleRepository saleRepository;

    public EmployeeAnalyticsService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
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
}