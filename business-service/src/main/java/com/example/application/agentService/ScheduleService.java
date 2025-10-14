//package com.example.application.agentService;
//
//
//import com.example.application.agentService.dto.ChatMessage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ScheduleService {
//
//    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
//
//    private final ChatAgent chatAgent;
//    private final RAGAgent ragAgent;
//
//    public ScheduleService(ChatAgent chatAgent, RAGAgent ragAgent) {
//        this.chatAgent = chatAgent;
//        this.ragAgent = ragAgent;
//    }
//
//    public String generateSchedule(String sessionId, String message) {
//        try {
//            log.info("Session [{}]: User message: {}", sessionId, message);
//
//            ChatMessage chatMessage = this.chatAgent.chat(sessionId, message).content();
//            log.info("Session [{}]: Chat agent response: {}", sessionId, chatMessage);
//
//            switch (chatMessage.state()) {
//                case CHAT -> {
//                    return chatMessage.messageToCustomer();
//                }
//                case RAG -> {
//                    String machineName = chatMessage.scheduleMachineName();
//                    log.info("Session [{}]: RAG agent machine name: {}", sessionId, machineName);
//                    String schedule = this.ragAgent.answer(sessionId, machineName).content();
//                    log.info("Session [{}]: RAG agent generated schedule: {}", sessionId, schedule);
//                    return schedule;
//                }
//                default -> {
//                    log.error("Session [{}]: Unknown state from chat agent: {}", sessionId, chatMessage.state());
//                    return "I'm sorry, but I'm having trouble processing your request right now. Please try again later.";
//                }
//            }
//        } catch (Exception e) {
//            log.error("Session [{}]: Error during scheduling process", sessionId, e);
//            return "I'm sorry, but I'm having trouble processing your request right now. Please try again later.";
//        }
//    }
//
//}
