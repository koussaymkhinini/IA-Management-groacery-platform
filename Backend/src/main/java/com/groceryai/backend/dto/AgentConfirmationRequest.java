
package com.groceryai.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AgentConfirmationRequest(
        @NotBlank(message = "Confirmation token cannot be empty.")
        String confirmationToken
) {}
