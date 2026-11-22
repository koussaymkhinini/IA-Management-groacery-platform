
package com.groceryai.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Conversation() {
        // Required by JPA
    }

    public Conversation(String title) {
        this.title = title;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (title == null || title.isBlank()) {
            title = "New conversation";
        }

        createdAt = now;
        updatedAt = now;
    }

    public void updateTitle(String title) {
        this.title = title;
        this.updatedAt = Instant.now();
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
