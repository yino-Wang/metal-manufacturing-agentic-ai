//package com.example.presentation.controller;
//
//
//import com.example.application.agentService.RAGAgent;
//import dev.langchain4j.service.Result;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class RAGAgentController {
//
//    private final RAGAgent RAGAgent;
//
//    public RAGAgentController(RAGAgent RAGAgent) {
//        this.RAGAgent = RAGAgent;
//    }
//
//    @GetMapping("/rag-schedule")
//    public String getSchedule(@RequestParam String sessionId, @RequestParam String userMessage) {
//        Result<String> result = RAGAgent.answer(sessionId, userMessage);
//        return result.content();
//    }
//}
