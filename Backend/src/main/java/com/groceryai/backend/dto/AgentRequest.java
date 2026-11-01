package com.groceryai.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AgentRequest(
        @NotBlank(message = "Message cannot be empty.")
        String message
) {}