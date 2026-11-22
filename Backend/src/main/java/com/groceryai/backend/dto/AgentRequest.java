package com.groceryai.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AgentRequest(

        @NotBlank(message = "Message cannot be empty.")
        String message,

        @NotNull(message = "Conversation ID cannot be null.")
        UUID conversationId

) {}