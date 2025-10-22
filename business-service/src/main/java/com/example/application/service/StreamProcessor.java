package com.example.application.service;

import com.example.events.JobAddedToMachineEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
//import com.example.interfaces.rest.JobAddedToMachineEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Consumer;

//IMPORTANT: code taken from CSCI318 lab example Cargo Tracker (Stream Processing) StreamProcessor class
//variable names have been changed and a composite serdeKey has been used instead
//" start quote

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
     * calculates the total material amounts per machine per materialName within 30-second windows,
     * and stores the result in a state store.
     *
     * @return a {@link Consumer} that processes the input stream.
     */
    @Bean
    public Consumer<KStream<String, JobAddedToMachineEvent>> process() {

        final MachineMaterialKeySerde keySerde = new MachineMaterialKeySerde();

        return inputStream -> {

            // Transform the input stream: extract machineId, materialName and material amount.
            KTable<Windowed<MachineMaterialKey>, Long> totalMaterials = inputStream.map((key, value) -> {
                        String machineId = value.getJobAddedToMachineEventData().getMachineId();
                        String materialName = value.getJobAddedToMachineEventData().getMaterialNeeded();
                        Long materialAmount = (long)value.getJobAddedToMachineEventData().getMaterialAmount();

                        MachineMaterialKey compositeKey = new MachineMaterialKey(machineId, materialName);
                        return KeyValue.pair(compositeKey, materialAmount);
                    }).
                    // Group events by the composite serdekey of materialName and machineId.
                    groupByKey(Grouped.with(keySerde, Serdes.Long())).
                    // Create 30-second tumbling windows for aggregation.
                    windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(30))).
                    // Sum the material amounts for each schedule within the window.
                    reduce(Long::sum,
                        // Materialize the result into a state store.
                        Materialized.<MachineMaterialKey, Long, WindowStore<Bytes, byte[]>>as(WINDOWSTORE_NAME).
                                withKeySerde(keySerde).withValueSerde(Serdes.Long()));

            // For debugging/monitoring: Print the aggregated results to the console.
            totalMaterials.toStream().
                    print(Printed.<Windowed<MachineMaterialKey>, Long>toSysOut().withLabel("Windowed material totals per schedule"));
        };
    }
}
//" end quote
