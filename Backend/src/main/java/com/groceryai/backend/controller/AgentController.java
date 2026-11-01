package com.groceryai.backend.controller;

import com.groceryai.backend.agent.AgentService;
import com.groceryai.backend.dto.AgentRequest;
import com.groceryai.backend.dto.AgentResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/chat")
    public AgentResponse chat(@Valid @RequestBody AgentRequest request) {

        String answer = agentService.ask(request.message());

        return new AgentResponse(answer);
    }
}