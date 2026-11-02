
package com.groceryai.backend.controller;

import com.groceryai.backend.agent.AgentMutationConfirmationService;
import com.groceryai.backend.dto.AgentConfirmationRequest;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentConfirmationController {

    private final AgentMutationConfirmationService confirmationService;

    public AgentConfirmationController(
            AgentMutationConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @PostMapping("/confirm")
    public Object confirm(
            @Valid @RequestBody AgentConfirmationRequest request) {

        return confirmationService.confirm(
                request.confirmationToken()
        );
    }
}
