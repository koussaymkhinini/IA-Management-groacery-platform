package com.groceryai.backend.controller;

import com.groceryai.backend.dto.ConversationResponse;
import com.groceryai.backend.dto.MessageResponse;
import com.groceryai.backend.entity.Conversation;
import com.groceryai.backend.entity.Message;
import com.groceryai.backend.service.ConversationService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationsController {

    private final ConversationService conversationService;

    public ConversationsController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ConversationResponse createConversation() {
        Conversation conversation =
                conversationService.createConversation();

        return toConversationResponse(conversation);
    }

    @GetMapping
    public List<ConversationResponse> getAllConversations() {
        return conversationService.getAllConversations()
                .stream()
                .map(this::toConversationResponse)
                .toList();
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponse> getMessages(@PathVariable UUID id) {
        return conversationService.getMessages(id)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deleteConversation(@PathVariable UUID id) {
        conversationService.deleteConversation(id);
    }

    private ConversationResponse toConversationResponse(
            Conversation conversation) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}