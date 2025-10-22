package com.example.application;

import com.example.interfaces.dto.MaterialConsumedByName;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.binder.kafka.streams.KafkaStreamsRegistry;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private RestTemplate restTemplate;

    private final InteractiveQueryService interactiveQueryService;
    @Value("${windowstore.name:inventory-windowstore}")
    private String WINDOWSTORE_NAME;
    @Value("${window.size.ms:30000}")
    private long WINDOW_SIZE_MS;


//    public MaterialUsageQueryService(StreamsBuilderFactoryBean streamsBuilderFactoryBean, InteractiveQueryService interactiveQueryService) {
//        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
//        this.interactiveQueryService = interactiveQueryService;
//    }

    // Use a constructor for injection.
    @Autowired
    public MaterialUsageQueryService(
            InteractiveQueryService interactiveQueryService,
            RestTemplate restTemplate,
            @Qualifier("materialUsageAggregator-builder") StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        this.interactiveQueryService = interactiveQueryService;
        this.restTemplate = restTemplate;
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    // This method checks the stream's state.
    public boolean areStreamsReady() {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        return kafkaStreams != null && kafkaStreams.state().isRunningOrRebalancing();
    }


    /**
     * Retrieves total material usage from the most recent completed window
     *
     * @return A list of {@link MaterialConsumedByName} objects
     */
    public List<MaterialConsumedByName> getRecentMaterialUsage() {
        List<MaterialConsumedByName> usageStats = new ArrayList<>();
        long now = Instant.now().toEpochMilli();

        long targetWindowStartTime = (now / WINDOW_SIZE_MS) * WINDOW_SIZE_MS - WINDOW_SIZE_MS;
        Instant timeFrom = Instant.ofEpochMilli(targetWindowStartTime);
        Instant timeTo = timeFrom.plus(Duration.ofMillis(WINDOW_SIZE_MS));

        try (KeyValueIterator<Windowed<String>, Long> iterator = getWindowStore().fetchAll(timeFrom, timeTo)) {
            while (iterator.hasNext()) {
                KeyValue<Windowed<String>, Long> record = iterator.next();
                MaterialConsumedByName amountPerMaterial = new MaterialConsumedByName();
                amountPerMaterial.setName(record.key.key());
                amountPerMaterial.setQuantity(record.value); // This value is the sum of booking amounts, not a quantity.
                usageStats.add(amountPerMaterial);
            }
        }

        return usageStats;
    }

    /**
     * Helper method to retrieve the read-only window store for job material analytics.
     * It uses the InteractiveQueryService to get a queryable handle to the state store.
     *
     * @return A {@link ReadOnlyWindowStore} that can be queried for aggregated material data.
     */
    private ReadOnlyWindowStore<String, Long> getWindowStore() {
        return this.interactiveQueryService.getQueryableStore(WINDOWSTORE_NAME,
                QueryableStoreTypes.windowStore());
    }
}