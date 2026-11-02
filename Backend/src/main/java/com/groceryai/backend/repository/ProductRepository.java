package com.groceryai.backend.repository;

import com.groceryai.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory_CategoryId(Integer categoryId);
    Page<Product> findByCategory_CategoryId(
        Integer categoryId,
        Pageable pageable
);
}