
package com.groceryai.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record AgentRequest(
        UUID conversationId,

        @NotBlank(message = "Message cannot be empty.")
        String message
) {}
