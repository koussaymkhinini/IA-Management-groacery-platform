
package com.groceryai.backend.repository;

import com.groceryai.backend.entity.ChatMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findByConversation_IdOrderByCreatedAtDesc(
            UUID conversationId,
            Pageable pageable
    );

    List<ChatMessage> findByConversation_IdOrderByCreatedAtAsc(
            UUID conversationId
    );
}
