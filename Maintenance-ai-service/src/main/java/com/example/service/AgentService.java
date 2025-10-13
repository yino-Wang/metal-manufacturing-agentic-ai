package com.example.service;

import com.example.service.dto.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final ChatAgent chatAgent;
    private final RAGAgent ragAgent;

    public AgentService(ChatAgent chatAgent, RAGAgent ragAgent) {
        this.chatAgent = chatAgent;
        this.ragAgent = ragAgent;
    }

    public String recommend(String sessionId, String message) {
        try {
            log.info("Session [{}]: User message: {}", sessionId, message);

            ChatMessage chatMessage = this.chatAgent.chat(sessionId, message).content();
            log.info("Session [{}]: Chat agent response: {}", sessionId, chatMessage);

            switch (chatMessage.state()) {
                case CHAT -> {
                    return chatMessage.messageToUser();
                }
                case RAG -> {
                    String issueDescription = chatMessage.issueDescription();
                    log.info("Session [{}]: RAG agent book description: {}", sessionId, issueDescription);
                    String recommendation = this.ragAgent.answer(sessionId, issueDescription).content();
                    log.info("Session [{}]: RAG agent recommendation: {}", sessionId, recommendation);
                    return recommendation;
                }
                default -> {
                    log.error("Session [{}]: Unknown state from chat agent: {}", sessionId, chatMessage.state());
                    return "I'm sorry, but I'm having trouble processing your request right now. Please try again later.";
                }
            }
        } catch (Exception e) {
            log.error("Session [{}]: Error during recommendation process", sessionId, e);
            return "I'm sorry, but I'm having trouble processing your request right now. Please try again later.";
        }
    }

}
