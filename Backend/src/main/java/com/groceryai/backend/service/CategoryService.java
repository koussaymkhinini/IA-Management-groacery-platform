package com.groceryai.backend.service;

import com.groceryai.backend.dto.CategoryResponse;
import com.groceryai.backend.dto.CreateCategoryRequest;
import com.groceryai.backend.dto.UpdateCategoryRequest;
import com.groceryai.backend.entity.Category;
import com.groceryai.backend.repository.CategoryRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // GET all categories
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET category by ID
    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with ID: " + id
                ));

        return toResponse(category);
    }

    // POST create category
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        if (request.categoryId() == null ||
                request.categoryName() == null ||
                request.categoryName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category ID and name are required."
            );
        }

        if (categoryRepository.existsById(request.categoryId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category already exists with ID: " + request.categoryId()
            );
        }

        Category category = new Category(
                request.categoryId(),
                request.categoryName().trim()
        );

        return toResponse(categoryRepository.save(category));
    }

    // PUT update category
    @Transactional
    public CategoryResponse updateCategory(
            Integer id,
            UpdateCategoryRequest request) {

        if (request.categoryName() == null ||
                request.categoryName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category name cannot be empty."
            );
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with ID: " + id
                ));

        category.updateName(request.categoryName().trim());

        return toResponse(categoryRepository.save(category));
    }

    // DELETE category
    @Transactional
    public void deleteCategory(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with ID: " + id
                ));

        try {
            categoryRepository.delete(category);
            categoryRepository.flush();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this category because products reference it."
            );
        }
    }

    // Entity to DTO conversion
    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName()
        );
    }
}