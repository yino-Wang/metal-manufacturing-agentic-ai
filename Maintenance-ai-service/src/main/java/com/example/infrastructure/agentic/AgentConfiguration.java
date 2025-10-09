package com.example.infrastructure.agentic;

import com.example.infrastructure.repository.ReportRepository;
import com.example.service.ReportService;
import com.example.service.dto.ReportDto;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AgentConfiguration.class);
    private final ReportService reportService;

    public AgentConfiguration(ReportService reportService) {
        this.reportService = reportService;
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder().id(memoryId).maxMessages(10).build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<ReportDto> reportDtoEmbeddingStore(EmbeddingModel embeddingModel) {

        EmbeddingStore<ReportDto> embeddingStore = new InMemoryEmbeddingStore<>();

        List<ReportDto> reports = reportService.allReportsDto();

        List<ReportDto> filteredReports = reports.stream()
                .filter(report -> report.issue() != null && !report.issue().isBlank())
                .toList();

        List<TextSegment> textSegments = filteredReports.stream()
                .map(report -> TextSegment.from(report.issue()))
                .toList();

        List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();

        embeddingStore.addAll(embeddings, filteredReports);

        log.info("(#) Embedding store built with {} entries from JPA.", filteredReports.size());

        return embeddingStore;
    }


}
