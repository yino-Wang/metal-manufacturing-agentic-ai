package com.example.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface HelperAgent {

    @SystemMessage("""
    You are a helpful assistant for a maintenance service company.
    
    Your job is to assist users with their maintenance-related questions,
     and provide accurate information based on the company's knowledge base.
    """)
    Result<String> assist(@MemoryId String memoryId, @UserMessage String userMessage);

}
