
package com.groceryai.backend.service;

import com.groceryai.backend.entity.ChatMessage;
import com.groceryai.backend.entity.Conversation;
import com.groceryai.backend.repository.ChatMessageRepository;
import com.groceryai.backend.repository.ConversationRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationMemoryService {

    private static final int DEFAULT_HISTORY_LIMIT = 10;
    private static final int MAX_HISTORY_LIMIT = 50;
    private static final int MAX_TITLE_LENGTH = 60;

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ConversationMemoryService(
            ConversationRepository conversationRepository,
            ChatMessageRepository chatMessageRepository) {

        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Retrieves an existing conversation or creates a new one.
     */
    @Transactional
    public Conversation getOrCreateConversation(
            UUID conversationId,
            String firstMessage) {

        if (conversationId != null) {
            return conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Conversation not found: " + conversationId
                    ));
        }

        String title = generateTitle(firstMessage);

        Conversation conversation = new Conversation(title);

        return conversationRepository.save(conversation);
    }

    /**
     * Persists a message inside an existing conversation.
     */
    @Transactional
    public ChatMessage saveMessage(
            UUID conversationId,
            ChatMessage.Role role,
            String content) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Conversation not found: " + conversationId
                ));

        ChatMessage message = new ChatMessage(
                conversation,
                role,
                content
        );

        ChatMessage savedMessage = chatMessageRepository.save(message);

        conversation.touch();
        conversationRepository.save(conversation);

        return savedMessage;
    }

    /**
     * Retrieves the most recent messages in chronological order.
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getRecentMessages(UUID conversationId) {

        return getRecentMessages(conversationId, DEFAULT_HISTORY_LIMIT);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getRecentMessages(
            UUID conversationId,
            int limit) {

        if (limit < 1 || limit > MAX_HISTORY_LIMIT) {
            throw new IllegalArgumentException(
                    "History limit must be between 1 and " + MAX_HISTORY_LIMIT
            );
        }

        List<ChatMessage> messages =
                chatMessageRepository.findByConversation_IdOrderByCreatedAtDesc(
                        conversationId,
                        PageRequest.of(0, limit)
                );

        // The repository returns newest first.
        // Reverse the list so the AI receives oldest first.
        Collections.reverse(messages);

        return messages;
    }

    /**
     * Generates a readable title from the first user message.
     */
    private String generateTitle(String firstMessage) {

        if (firstMessage == null || firstMessage.isBlank()) {
            return "New conversation";
        }

        String title = firstMessage.trim().replaceAll("\\s+", " ");

        if (title.length() > MAX_TITLE_LENGTH) {
            title = title.substring(0, MAX_TITLE_LENGTH - 3) + "...";
        }

        return title;
    }
    
/**
 * Retrieves all conversations, newest activity first.
 */
@Transactional(readOnly = true)
public List<Conversation> getAllConversations() {
    return conversationRepository.findAllByOrderByUpdatedAtDesc();
}

/**
 * Retrieves every message in a conversation, oldest first.
 */
@Transactional(readOnly = true)
public List<ChatMessage> getAllMessages(UUID conversationId) {

    if (!conversationRepository.existsById(conversationId)) {
        throw new EntityNotFoundException(
                "Conversation not found: " + conversationId
        );
    }

    return chatMessageRepository
            .findByConversation_IdOrderByCreatedAtAsc(conversationId);
}

}
