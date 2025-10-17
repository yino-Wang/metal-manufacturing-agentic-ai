package com.example.service;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.*;
import org.springframework.stereotype.Service;

/**
 * Calls the AI model (via LangChain4j) to recommend next month's restock quantity.
 */
@Service
public class AIRecommendationService {

    private final AIRecommender aiRecommender;

    public AIRecommendationService() {
        // Use your OpenAI key (set in environment variable OPENAI_API_KEY)
        var model = OpenAiChatModel.withApiKey(System.getenv("OPENAI_API_KEY"));
        this.aiRecommender = AiServices.create(AIRecommender.class, model);
    }

    @AiService
    public interface AIRecommender {
        @SystemMessage("""
            You are an AI supply planner.
            Given the material name, current stock, and reorder threshold,
            estimate how much stock is needed for the next month to maintain adequate supply.
            Respond concisely: e.g., "Order 250 units – based on average consumption and lead time."
        """)
        String recommend(@UserMessage String message);
    }

    public String getRecommendation(String name, int currentQty, int reorderPoint) {
        String prompt = String.format(
                "Material: %s, Current stock: %d, Reorder point: %d",
                name, currentQty, reorderPoint
        );
        return aiRecommender.recommend(prompt);
    }
}
