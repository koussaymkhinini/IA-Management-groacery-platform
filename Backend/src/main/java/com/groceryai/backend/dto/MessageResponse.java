package com.groceryai.backend.dto;

import com.groceryai.backend.entity.MessageRole;
import java.time.Instant;

public record MessageResponse(
        Long id,
        MessageRole role,
        String content,
        Instant createdAt
) {}