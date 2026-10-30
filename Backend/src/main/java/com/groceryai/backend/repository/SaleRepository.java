package com.groceryai.backend.repository;

import com.groceryai.backend.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByCustomer_CustomerId(Integer customerId);

    List<Sale> findBySalesperson_EmployeeId(Integer employeeId);

    List<Sale> findByProduct_ProductId(Integer productId);
}