package com.groceryai.backend.repository;

import com.groceryai.backend.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByCustomer_CustomerId(Integer customerId);

    List<Sale> findBySalesperson_EmployeeId(Integer employeeId);

    List<Sale> findByProduct_ProductId(Integer productId);

    @Query("""
            SELECT
                COUNT(s) AS totalSales,
                COALESCE(SUM(s.quantity), 0) AS totalQuantitySold,
                COALESCE(SUM(
                    CASE WHEN s.totalPrice > 0 THEN 1 ELSE 0 END
                ), 0) AS salesWithRecordedRevenue,
                COALESCE(SUM(
                    CASE WHEN s.totalPrice IS NULL OR s.totalPrice = 0
                         THEN 1 ELSE 0 END
                ), 0) AS salesWithoutRecordedRevenue,
                COALESCE(SUM(s.totalPrice), 0) AS recordedRevenue
            FROM Sale s
            """)
    SalesOverviewProjection getSalesOverview();

    @Query(value = """
            SELECT
                TO_CHAR(DATE_TRUNC('month', sales_date), 'YYYY-MM')
                    AS "month",
                COUNT(*) AS "totalSales",
                COALESCE(SUM(quantity), 0) AS "totalQuantitySold",
                COALESCE(SUM(total_price), 0) AS "recordedRevenue",
                COUNT(*) FILTER (
                    WHERE total_price > 0
                ) AS "salesWithRecordedRevenue",
                COUNT(*) FILTER (
                    WHERE total_price = 0 OR total_price IS NULL
                ) AS "salesWithoutRecordedRevenue"
            FROM sales
            GROUP BY DATE_TRUNC('month', sales_date)
            ORDER BY DATE_TRUNC('month', sales_date)
            """, nativeQuery = true)
    List<SalesRevenueOverTimeProjection> getRevenueOverTime();
}