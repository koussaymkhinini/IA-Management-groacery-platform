package com.groceryai.backend.dto;

import java.util.UUID;

public record AgentResponse(
        UUID conversationId,
        String answer,
        ChartData chart
) {
    public AgentResponse(UUID conversationId, String answer) {
        this(conversationId, answer, null);
    }
}