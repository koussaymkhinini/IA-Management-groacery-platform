
package com.groceryai.backend.controller;

import com.groceryai.backend.agent.AgentService;
import com.groceryai.backend.dto.AgentRequest;
import com.groceryai.backend.dto.AgentResponse;
import com.groceryai.backend.entity.ChatMessage;
import com.groceryai.backend.entity.Conversation;
import com.groceryai.backend.service.ConversationMemoryService;
import com.groceryai.backend.dto.ChatMessageResponse;
import com.groceryai.backend.dto.ConversationResponse;
import java.util.UUID;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agent")
@CrossOrigin(origins = "http://localhost:5173")
public class AgentController {

    private static final Logger log =
            LoggerFactory.getLogger(AgentController.class);

    private final AgentService agentService;
    private final ConversationMemoryService memoryService;

    public AgentController(
            AgentService agentService,
            ConversationMemoryService memoryService) {

        this.agentService = agentService;
        this.memoryService = memoryService;
    }
    @GetMapping("/conversations")
public List<ConversationResponse> getConversations() {
    return memoryService.getAllConversations()
            .stream()
            .map(conversation -> new ConversationResponse(
                    conversation.getId(),
                    conversation.getTitle(),
                    conversation.getCreatedAt(),
                    conversation.getUpdatedAt()
            ))
            .toList();
}

@GetMapping("/conversations/{conversationId}/messages")
public List<ChatMessageResponse> getMessages(
        @PathVariable UUID conversationId
) {
    return memoryService.getAllMessages(conversationId)
            .stream()
            .map(message -> new ChatMessageResponse(
                    message.getId(),
                    message.getRole().name(),
                    message.getContent(),
                    message.getCreatedAt()
            ))
            .toList();
}
    @PostMapping("/chat")
    public AgentResponse chat(@Valid @RequestBody AgentRequest request) {

        long start = System.currentTimeMillis();

        log.info("REQUEST RECEIVED");

        try {
            // 1. Retrieve an existing conversation or create a new one.
            Conversation conversation =
                    memoryService.getOrCreateConversation(
                            request.conversationId(),
                            request.message()
                    );

            // 2. Retrieve previous messages BEFORE saving the current one.
            List<ChatMessage> history =
                    memoryService.getRecentMessages(conversation.getId());

            // 3. Persist the current user message.
            memoryService.saveMessage(
                    conversation.getId(),
                    ChatMessage.Role.USER,
                    request.message()
            );

            // 4. Generate an answer using the conversation history.
            String answer = agentService.ask(
                    request.message(),
                    history
            );

            // 5. Persist the assistant response.
            memoryService.saveMessage(
                    conversation.getId(),
                    ChatMessage.Role.ASSISTANT,
                    answer
            );

            // 6. Return the conversation ID and answer.
            return new AgentResponse(
                    conversation.getId(),
                    answer
            );

        } finally {
            log.info(
                    "REQUEST COMPLETED IN {} ms",
                    System.currentTimeMillis() - start
            );
        }
    }
}
