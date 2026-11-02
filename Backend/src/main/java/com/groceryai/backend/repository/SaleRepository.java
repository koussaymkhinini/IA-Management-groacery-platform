package com.groceryai.backend.repository;

import com.groceryai.backend.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByCustomer_CustomerId(Integer customerId);

    List<Sale> findBySalesperson_EmployeeId(Integer employeeId);

    List<Sale> findByProduct_ProductId(Integer productId);

    Page<Sale> findByCustomer_CustomerId(
        Integer customerId,
        Pageable pageable
    );

    Page<Sale> findBySalesperson_EmployeeId(
            Integer employeeId,
            Pageable pageable
    );

    Page<Sale> findByProduct_ProductId(
            Integer productId,
            Pageable pageable
    );

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

    @Query("""
        SELECT
            e.employeeId AS employeeId,
            CONCAT(e.firstName, ' ', e.lastName) AS employeeName,
            COUNT(s) AS totalSales,
            SUM(s.quantity) AS totalQuantitySold
        FROM Sale s
        JOIN s.salesperson e
        GROUP BY e.employeeId, e.firstName, e.lastName
        ORDER BY SUM(s.quantity) DESC
        """)
    List<EmployeePerformanceProjection> getEmployeePerformance();

    @Query("""
        SELECT
            c.customerId AS customerId,
            CONCAT(c.firstName, ' ', c.lastName) AS customerName,
            COUNT(s) AS totalPurchases,
            SUM(s.quantity) AS totalQuantityPurchased
        FROM Sale s
        JOIN s.customer c
        GROUP BY c.customerId, c.firstName, c.lastName
        ORDER BY COUNT(s) DESC
        """)
    List<TopCustomerProjection> getTopCustomers();

    @Query("""
        SELECT
            c.categoryId AS categoryId,
            c.categoryName AS categoryName,
            SUM(s.quantity) AS totalQuantitySold,
            COUNT(s) AS totalSales
        FROM Sale s
        JOIN s.product p
        JOIN p.category c
        GROUP BY c.categoryId, c.categoryName
        ORDER BY SUM(s.quantity) DESC
        """)
    List<SalesByCategoryProjection> getSalesByCategory();

    @Query("""
        SELECT
            p.productId AS productId,
            p.productName AS productName,
            SUM(s.quantity) AS totalQuantitySold,
            COUNT(s) AS totalSales
        FROM Sale s
        JOIN s.product p
        GROUP BY p.productId, p.productName
        ORDER BY SUM(s.quantity) DESC
        """)
    List<TopSellingProductProjection> getTopSellingProducts();

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
    @Query(value = """
            SELECT *
            FROM sales
            ORDER BY sales_date DESC NULLS LAST, sales_id DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<Sale> findLatestSale();
}