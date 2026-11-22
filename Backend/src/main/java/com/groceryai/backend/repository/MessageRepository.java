
package com.groceryai.backend.repository;

import com.groceryai.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversation_IdOrderByCreatedAtAsc(UUID conversationId);
}
