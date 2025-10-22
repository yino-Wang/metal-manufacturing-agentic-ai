//package com.example.application;
//
//import com.example.events.JobAddedToMachineEvent;
//import org.apache.kafka.common.utils.Bytes;
//import org.apache.kafka.streams.kstream.*;
//import org.apache.kafka.streams.state.WindowStore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.Duration;
//
///**
// * Kafka Streams processor that aggregates total material usage over 30-second windows.
// * Produces a window store that can later be queried by InteractiveQueryService.
// */
//@Configuration
//public class MaterialUsageStreamProcessor {
//
//    @Bean
//    public java.util.function.Consumer<KStream<String, JobAddedToMachineEvent>> materialUsageAggregator() {
//        return inputStream -> inputStream
//                .map((key, event) -> {
//                    String material = event.getJobAddedToMachineEventData().getMaterialNeeded();
//                    int amount = event.getJobAddedToMachineEventData().getMaterialAmount();
//                    return KeyValue.pair(material, amount);
//                })
//                .groupByKey(Grouped.with(
//                        org.apache.kafka.common.serialization.Serdes.String(),
//                        org.apache.kafka.common.serialization.Serdes.Long()
//                ))
//                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(30)))
//                .reduce(Long::sum, MaterialUsageStreamProcessor::materializeStore);
//    }
//
//    private static Materialized<String, Long, WindowStore<Bytes, byte[]>> materializeStore() {
//        return Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("material-usage-window-store")
//                .withKeySerde(org.apache.kafka.common.serialization.Serdes.String())
//                .withValueSerde(org.apache.kafka.common.serialization.Serdes.Long());
//    }
//}
