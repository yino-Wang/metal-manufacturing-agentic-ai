package com.example.application;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Queries the Kafka Streams window store for aggregated material usage.
 */
@Service
public class MaterialUsageQueryService {

    private final InteractiveQueryService interactiveQueryService;

    @Value("${windowstore.name}")
    private String windowStoreName;

    @Value("${window.size.ms}")
    private long windowSizeMs;

    public MaterialUsageQueryService(InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
    }

    /**
     * Retrieves total material usage from the most recent completed window.
     */
    public List<Map.Entry<String, Long>> getRecentMaterialUsage() {
        List<Map.Entry<String, Long>> usageStats = new ArrayList<>();
        long now = Instant.now().toEpochMilli();

        long targetWindowStartTime = (now / windowSizeMs) * windowSizeMs - windowSizeMs;
        Instant timeFrom = Instant.ofEpochMilli(targetWindowStartTime);
        Instant timeTo = timeFrom.plus(Duration.ofMillis(windowSizeMs));

        try (KeyValueIterator<Windowed<String>, Long> iterator = getWindowStore().fetchAll(timeFrom, timeTo)) {
            while (iterator.hasNext()) {
                KeyValue<Windowed<String>, Long> record = iterator.next();
                String material = record.key.key();
                Long totalUsed = record.value;
                usageStats.add(Map.entry(material, totalUsed));
            }
        }

        return usageStats;
    }

    private ReadOnlyWindowStore<String, Long> getWindowStore() {
        return interactiveQueryService.getQueryableStore(windowStoreName, QueryableStoreTypes.windowStore());
    }
}
