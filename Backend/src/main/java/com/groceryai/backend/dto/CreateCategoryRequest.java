package com.groceryai.backend.dto;

public record CreateCategoryRequest(
        Integer categoryId,
        String categoryName
) {}