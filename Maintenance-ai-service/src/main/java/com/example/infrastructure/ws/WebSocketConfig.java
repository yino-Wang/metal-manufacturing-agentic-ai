package com.example.infrastructure.ws;

import com.example.presentation.websocket.ChatWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    private final ChatWebSocketHandler handler;

    public WebSocketConfig(ChatWebSocketHandler handler) {
        this.handler = handler;
    }

    @Bean
    public SimpleUrlHandlerMapping handlerMapping() {
        return new SimpleUrlHandlerMapping(Map.of("/chat", handler), 10);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}
