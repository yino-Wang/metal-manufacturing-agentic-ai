package com.example.infrastructure.agentic;

import com.example.infrastructure.repository.ReportRepository;
import com.example.model.Report;
import com.example.service.ChatAgent;
import com.example.service.RAGAgent;
import com.example.service.ReportService;
import com.example.service.dto.ReportDto;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class AgentConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AgentConfiguration.class);

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder().id(memoryId).maxMessages(10).build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel, ReportService reportService) {

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        List<ReportDto> reports = reportService.allReportsDto();

        List<ReportDto> filteredReports = reports.stream()
                .filter(report -> report.solution() != null && !report.solution().isBlank())
                .toList();

        List<TextSegment> textSegments = filteredReports.stream()
                .map(report -> TextSegment.from(
                        String.join(" | ",
                                "Machine Id: " + java.util.Objects.toString(report.machineId(), ""),
                                "Issue: " + java.util.Objects.toString(report.issue(), ""),
                                "Solution: " + java.util.Objects.toString(report.solution(), "")
                        )
                ))
                .toList();

        List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();

        embeddingStore.addAll(embeddings, textSegments);

        log.info("(#) Embedding store built with {} entries from JPA.", filteredReports.size());

        return embeddingStore;
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

        int maxResults = 10;
        double minScore = 0.6;

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }

    @Bean
    RAGAgent solutionRecommendationAgent(ChatModel chatModel,
                                         ContentRetriever contentRetriever,
                                         ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(RAGAgent.class)
                .chatModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    @Bean
    ChatAgent chatAgent(ChatModel chatModel,
                        ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(ChatAgent.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

}
