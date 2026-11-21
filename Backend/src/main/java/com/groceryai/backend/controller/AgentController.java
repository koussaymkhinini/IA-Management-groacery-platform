package com.groceryai.backend.controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groceryai.backend.agent.AgentService;
import com.groceryai.backend.dto.AgentRequest;
import com.groceryai.backend.dto.AgentResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    private static final Logger log =
            LoggerFactory.getLogger(AgentController.class);

    @PostMapping("/chat")
    public AgentResponse chat(@Valid @RequestBody AgentRequest request) {

        long start = System.currentTimeMillis();

        log.info("REQUEST RECEIVED");

        try {
            String answer = agentService.ask(request.message());

            return new AgentResponse(answer);

        } finally {
            long duration = System.currentTimeMillis() - start;

            log.info("REQUEST COMPLETED IN {} ms", duration);
        }
    }
}