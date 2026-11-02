
package com.groceryai.backend.agent;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class AgentMutationConfirmationService {

    private static final long EXPIRATION_MINUTES = 5;

    private final ConcurrentHashMap<String, PendingMutation> pendingMutations =
            new ConcurrentHashMap<>();

    private record PendingMutation(
            String description,
            Supplier<?> operation,
            Instant expiresAt
    ) {}

    public String prepare(String description, Supplier<?> operation) {

        String token = UUID.randomUUID().toString();

        PendingMutation mutation = new PendingMutation(
                description,
                operation,
                Instant.now().plusSeconds(EXPIRATION_MINUTES * 60)
        );

        pendingMutations.put(token, mutation);

        return """
                Operation prepared but NOT executed.

                Description: %s

                Confirmation token: %s

                To execute this operation, send a POST request to:
                /api/v1/agent/confirm

                Request body:
                {"confirmationToken":"%s"}

                This token expires in 5 minutes.
                """.formatted(description, token, token);
    }

    public Object confirm(String token) {

        PendingMutation mutation = pendingMutations.remove(token);

        if (mutation == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid or already used confirmation token."
            );
        }

        if (Instant.now().isAfter(mutation.expiresAt())) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "Confirmation token has expired."
            );
        }

        return mutation.operation().get();
    }
}
