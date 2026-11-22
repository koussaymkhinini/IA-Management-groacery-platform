
package com.groceryai.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameConversationRequest(
        @NotBlank(message = "Title cannot be empty.")
        @Size(max = 200, message = "Title cannot exceed 200 characters.")
        String title
) {}
