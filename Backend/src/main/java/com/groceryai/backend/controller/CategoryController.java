package com.groceryai.backend.controller;

import com.groceryai.backend.dto.CategoryResponse;
import com.groceryai.backend.dto.CreateCategoryRequest;
import com.groceryai.backend.dto.UpdateCategoryRequest;
import com.groceryai.backend.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET all categories
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // GET category by ID
    @GetMapping("/{id:\\d+}")
    public CategoryResponse getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }

    // POST create category
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CreateCategoryRequest request) {

        CategoryResponse response =
                categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUT update category
    @PutMapping("/{id:\\d+}")
    public CategoryResponse updateCategory(
            @PathVariable Integer id,
            @RequestBody UpdateCategoryRequest request) {

        return categoryService.updateCategory(id, request);
    }

    // DELETE category
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}