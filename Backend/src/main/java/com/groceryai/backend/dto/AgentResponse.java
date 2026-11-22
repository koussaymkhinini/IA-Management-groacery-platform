
package com.groceryai.backend.dto;

import java.util.UUID;

public record AgentResponse(
        UUID conversationId,
        String answer
) {}
