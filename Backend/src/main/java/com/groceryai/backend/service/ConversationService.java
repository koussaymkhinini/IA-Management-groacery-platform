package com.groceryai.backend.service;

import com.groceryai.backend.entity.Conversation;
import com.groceryai.backend.entity.Message;
import com.groceryai.backend.entity.MessageRole;
import com.groceryai.backend.repository.ConversationRepository;
import com.groceryai.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Conversation createConversation() {
        return conversationRepository.save(
                new Conversation("New conversation")
        );
    }


    @Transactional
    public Message saveMessage(
            UUID conversationId,
            MessageRole role,
            String content) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Conversation not found"));

        // Generate the conversation title from the first user message.
        if (role == MessageRole.USER &&
                "New conversation".equals(conversation.getTitle())) {

            String title = content.trim();

            if (title.length() > 50) {
                title = title.substring(0, 47) + "...";
            }

            conversation.updateTitle(title);
        } else {
            conversation.touch();
        }

        Message message = new Message(conversation, role, content);

        return messageRepository.save(message);
    }


    @Transactional(readOnly = true)
    public List<Conversation> getAllConversations() {
        return conversationRepository.findAllByOrderByUpdatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Message> getMessages(UUID conversationId) {

        if (!conversationRepository.existsById(conversationId)) {
            throw new IllegalArgumentException("Conversation not found");
        }

        return messageRepository
                .findByConversation_IdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public void deleteConversation(UUID conversationId) {

        if (!conversationRepository.existsById(conversationId)) {
            throw new IllegalArgumentException("Conversation not found");
        }

        conversationRepository.deleteById(conversationId);
    }

    @Transactional
    public Conversation renameConversation(UUID conversationId, String title) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Conversation not found"));

        conversation.updateTitle(title.trim());

        return conversationRepository.save(conversation);
    }
}