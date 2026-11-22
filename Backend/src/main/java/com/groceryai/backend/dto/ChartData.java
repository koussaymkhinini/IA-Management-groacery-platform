package com.groceryai.backend.dto;

import java.util.List;
import java.util.Map;

public record ChartData(
        String type,
        String title,
        String xKey,
        String yKey,
        List<Map<String, Object>> data
) {}