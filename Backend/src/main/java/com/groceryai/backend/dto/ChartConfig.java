package com.groceryai.backend.dto;

public record ChartConfig(
        String type,
        String title,
        String xKey,
        String yKey
) {}