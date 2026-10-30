package com.groceryai.backend.service;

import org.springframework.transaction.annotation.Transactional;
import com.groceryai.backend.dto.ProductResponse;
import com.groceryai.backend.entity.Category;
import com.groceryai.backend.entity.Product;
import com.groceryai.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + id
                ));

        return toResponse(product);
    }

    public List<ProductResponse> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product product) {
        Category category = product.getCategory();

        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                category.getCategoryId(),
                category.getCategoryName(),
                product.getProductClass(),
                product.getModifyDate(),
                product.getResistant(),
                product.getIsAllergic(),
                product.getVitalityDays()
        );
    }
}