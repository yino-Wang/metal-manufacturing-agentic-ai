package com.example.application.service;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import com.example.interfaces.rest.JobAddedToMachineEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A Spring Configuration class that defines a Kafka Streams processor.
 * This class is responsible for processing streams of {@link JobAddedToMachineEvent}s
 * to provide real-time analytics on jobs.
 */
@Configuration
public class StreamProcessor {

    @Value("${windowstore.name}")
    private String WINDOWSTORE_NAME;

    /**
     * Defines the core stream processing logic.
     * This method consumes a {@link KStream} of {@link JobAddedToMachineEvent}s,
     * calculates the total booking amounts per destination city within 30-second windows,
     * and stores the result in a state store.
     *
     * @return a {@link Consumer} that processes the input stream.
     */
    @Bean
    public Consumer<KStream<String, JobAddedToMachineEvent>> process() {
        return inputStream -> {

            // Transform the input stream: extract schedulingId and material amount.
            KTable<Windowed<String>, Long> totalMaterials = inputStream.map((key, value) -> {
                        String schedulingId = value.getJobAddedToMachineEventData().getMachineId();
                        Long materialAmount = (long)value.getJobAddedToMachineEventData().getMaterialAmount();
                        return KeyValue.pair(schedulingId, materialAmount);
                    }).
                    // Group events by the schedulingId.
                    groupByKey(Grouped.with(Serdes.String(), Serdes.Long())).
                    // Create 30-second tumbling windows for aggregation.
                    windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(30))).
                    // Sum the material amounts for each schedule within the window.
                    reduce(Long::sum,
                        // Materialize the result into a state store.
                        Materialized.<String, Long, WindowStore<Bytes, byte[]>>as(WINDOWSTORE_NAME).
                                withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()));

            // For debugging/monitoring: Print the aggregated results to the console.
            totalMaterials.toStream().
                    print(Printed.<Windowed<String>, Long>toSysOut().withLabel("Windowed material totals per schedule"));
        };
    }
}
