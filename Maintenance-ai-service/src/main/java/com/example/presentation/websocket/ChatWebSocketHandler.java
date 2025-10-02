package com.example.presentation.websocket;

import com.example.service.ReportService;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ReportService reportService;

    public ChatWebSocketHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();

        return session.send(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .map(text -> {
                            // delegate to service
                            String reply = reportService.recommend(sessionId, text);
                            return session.textMessage(reply);
                        })
        );
    }
}
