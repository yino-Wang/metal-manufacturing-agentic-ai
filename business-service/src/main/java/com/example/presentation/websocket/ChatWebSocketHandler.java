//package com.example.presentation.websocket;
//
//
//import com.example.application.agentService.ScheduleService;
//import org.springframework.web.reactive.socket.WebSocketHandler;
//import org.springframework.web.reactive.socket.WebSocketMessage;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//@Component
//public class ChatWebSocketHandler implements WebSocketHandler {
//
//    private final ScheduleService scheduleService;
//
//    public ChatWebSocketHandler(ScheduleService scheduleService) {
//        this.scheduleService = scheduleService;
//    }
//
//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//        String sessionId = session.getId();
//
//        return session.send(
//                session.receive()
//                        .map(WebSocketMessage::getPayloadAsText)
//                        .map(text -> {
//                            // delegate to service
//                            String reply = scheduleService.generateSchedule(sessionId, text);
//                            return session.textMessage(reply);
//                        })
//        );
//    }
//}
