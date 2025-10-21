package com.example.application;

import com.example.interfaces.events.transform.JobAddedToMachineEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Consumer;

@Configuration
public class StreamProcessor {

    @Bean
    public Consumer<KStream<String, JobAddedToMachineEvent>> process() {
        return inputStream -> {
            inputStream
                    .map((key, value) -> {
                        System.out.println("--------------------------------------------------");
                        System.out.println("[Stream] Received Job Event:");
                        System.out.println("→ Material: " + value.getMaterialName());
                        System.out.println("→ Quantity Required: " + value.getMaterialRequired());
                        System.out.println("--------------------------------------------------");
                        return KeyValue.pair(value.getMaterialName(), (int) value.getMaterialRequired());
                    })
                    .groupByKey()
                    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(30)))
                    .reduce(Long::sum)
                    .toStream()
                    .foreach((windowedKey, totalUsed) -> {
                        System.out.println("[Stream Window] Material: " + windowedKey.key()
                                + " | Total used in last 30s: " + totalUsed);
                    });
        };
    }
}
