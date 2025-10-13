package com.example.controller;

import com.example.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatAgentController {

    private final AgentService agentService;

    @Autowired
    public ChatAgentController(AgentService agentService){
        this.agentService = agentService;
    }

    @PostMapping("/chat-recommend")
    public String getRecommendation(@RequestBody ChatRequest request) {
        return this.agentService.recommend(request.getSessionId(), request.getUserMessage());
    }

    public static class ChatRequest {
        private String sessionId;
        private String userMessage;

        // getters and setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserMessage() { return userMessage; }
        public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
    }
}
