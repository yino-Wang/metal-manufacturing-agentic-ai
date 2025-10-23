package com.example.application;


import com.example.events.sharedDomain.JobAddedToMachineEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

//IMPORTANT: code taken from CSCI318 lab example Cargo Tracker (Stream Processing) StreamProcessor class
//variable names have been changed
//" start quote

/**
 * Kafka Streams processor that aggregates total material usage over 30-second windows.
 * Produces a window store that can later be queried by InteractiveQueryService.
 */
@Configuration
public class MaterialUsageStreamProcessor {

    @Value("${windowstore.name}")
    private String WINDOWSTORE_NAME;

    @Bean
    public java.util.function.Consumer<KStream<String, JobAddedToMachineEvent>> process() {
        return inputStream -> {
            KTable<Windowed<String>, Long> totalBookings = inputStream.map((key, value) -> {
                        String material = value.getJobAddedToMachineEventData().getMaterialNeeded();
                        Long amount = (long) value.getJobAddedToMachineEventData().getMaterialAmount();
                        return KeyValue.pair(material, amount);
                    })
                    .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                    // gets info for past 30 seconds and sums
                    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(30)))
                    .reduce(Long::sum, // Materialize the result into a state store.
                            Materialized.<String, Long, WindowStore<Bytes, byte[]>>as(WINDOWSTORE_NAME).
                                    withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()));


            // print the aggregated results to the console.
            totalBookings.toStream().
                    print(Printed.<Windowed<String>, Long>toSysOut().withLabel("Windowed material usage"));
        };
    }
}

//" end quote