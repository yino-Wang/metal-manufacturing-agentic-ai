//package com.example.infrastructure.agentic;
//
//
//import com.example.application.agentService.ChatAgent;
//import com.example.application.agentService.RAGAgent;
//import com.example.domain.model.aggreates.Machine;
//import dev.langchain4j.data.document.Document;
//import dev.langchain4j.data.document.parser.TextDocumentParser;
//import dev.langchain4j.data.embedding.Embedding;
//import dev.langchain4j.data.segment.TextSegment;
//import dev.langchain4j.memory.chat.ChatMemoryProvider;
//import dev.langchain4j.memory.chat.MessageWindowChatMemory;
//import dev.langchain4j.model.chat.ChatModel;
//import dev.langchain4j.model.embedding.EmbeddingModel;
//import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
//import dev.langchain4j.rag.content.retriever.ContentRetriever;
//import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
//import dev.langchain4j.service.AiServices;
//import dev.langchain4j.store.embedding.EmbeddingStore;
//import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
//
//@Configuration
//public class AgentConfiguration {
//
//    private static final Logger log = LoggerFactory.getLogger(AgentConfiguration.class);
//
//    @Bean
//    ChatMemoryProvider chatMemoryProvider() {
//        return memoryId -> MessageWindowChatMemory.builder()
//                .id(memoryId)
//                .maxMessages(10)
//                .build();
//    }
//
//    @Bean
//    EmbeddingModel embeddingModel() {
//        // Not the best embedding model, but good enough for this demo
//        return new AllMiniLmL6V2EmbeddingModel();
//    }
//
//    @Bean
//    EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel, ResourceLoader resourceLoader) throws IOException {
//
//        // 1. Create an in-memory embedding store
//        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//
//        // 2. Load the document - want to load in repository instead to get JobList
//        //------------------------------------------------------------------------------------------------------------
//        Resource resource = resourceLoader.getResource(Machine.getJobList().getJobs());
//        Document document = loadDocument(resource.getFile().toPath(), new TextDocumentParser());
//
//        // 3. Split the document into lines and create a TextSegment for each line
//        String[] lines = document.text().split("\r?\n");
//        List<TextSegment> segments = Arrays.stream(lines)
//                .filter(line -> !line.trim().isEmpty())
//                .map(TextSegment::from)
//                .collect(Collectors.toList());
//
//        // 4. Embed the segments and add them to the store
//        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
//        embeddingStore.addAll(embeddings, segments);
//
//        log.info("(#) Embedding store built with {} entries.", segments.size());
//
//        return embeddingStore;
//    }
//
//    @Bean
//    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
//
//        // You will need to adjust these parameters to find the optimal setting,
//        // which will depend on multiple factors, for example:
//        // - The nature of your data
//        // - The embedding model you are using
//        int maxResults = 10;
//        double minScore = 0.6;
//
//        return EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(embeddingStore)
//                .embeddingModel(embeddingModel)
//                .maxResults(maxResults)
//                .minScore(minScore)
//                .build();
//    }
//
//    @Bean
//    RAGAgent bookRecommendationAgent(ChatModel chatModel,
//                                     ContentRetriever contentRetriever,
//                                     ChatMemoryProvider chatMemoryProvider) {
//        return AiServices.builder(RAGAgent.class)
//                .chatModel(chatModel)
//                .contentRetriever(contentRetriever)
//                .chatMemoryProvider(chatMemoryProvider)
//                .build();
//    }
//
//    @Bean
//    ChatAgent chatAgent(ChatModel chatModel,
//                        ChatMemoryProvider chatMemoryProvider) {
//        return AiServices.builder(ChatAgent.class)
//                .chatModel(chatModel)
//                .chatMemoryProvider(chatMemoryProvider)
//                .build();
//    }
//
//}
