package com.groceryai.backend.service;

import com.groceryai.backend.dto.CreateProductRequest;
import com.groceryai.backend.dto.ProductResponse;
import com.groceryai.backend.dto.UpdateProductRequest;
import com.groceryai.backend.entity.Category;
import com.groceryai.backend.entity.Product;
import com.groceryai.backend.repository.CategoryRepository;
import com.groceryai.backend.repository.ProductRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // GET all products
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET product by ID
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + id
                ));

        return toResponse(product);
    }

    // GET products by category
    public List<ProductResponse> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // POST create product
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        if (request.productId() == null ||
                request.productName() == null ||
                request.productName().isBlank() ||
                request.categoryId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product ID, name, and category ID are required."
            );
        }

        if (productRepository.existsById(request.productId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product already exists with ID: " + request.productId()
            );
        }

        Category category = findCategory(request.categoryId());

        Product product = new Product(
                request.productId(),
                request.productName().trim(),
                request.price(),
                category,
                request.productClass(),
                request.modifyDate(),
                request.resistant(),
                request.isAllergic(),
                request.vitalityDays()
        );

        return toResponse(productRepository.saveAndFlush(product));
    }

    // PUT update product
    @Transactional
    public ProductResponse updateProduct(
            Integer id,
            UpdateProductRequest request) {

        if (request.productName() == null ||
                request.productName().isBlank() ||
                request.categoryId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product name and category ID are required."
            );
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + id
                ));

        Category category = findCategory(request.categoryId());

        product.update(
                request.productName().trim(),
                request.price(),
                category,
                request.productClass(),
                request.modifyDate(),
                request.resistant(),
                request.isAllergic(),
                request.vitalityDays()
        );

        return toResponse(productRepository.saveAndFlush(product));
    }

    // DELETE product
    @Transactional
    public void deleteProduct(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + id
                ));

        try {
            productRepository.delete(product);
            productRepository.flush();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this product because sales reference it."
            );
        }
    }

    // Find category or return 404
    private Category findCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with ID: " + categoryId
                ));
    }

    // Entity to DTO conversion
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getCategory().getCategoryId(),
                product.getCategory().getCategoryName(),
                product.getProductClass(),
                product.getModifyDate(),
                product.getResistant(),
                product.getIsAllergic(),
                product.getVitalityDays()
        );
    }
}