package com.example.application.service;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.Serdes;

public class MachineMaterialKeySerde extends Serdes.WrapperSerde<MachineMaterialKey> {
    public MachineMaterialKeySerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(MachineMaterialKey.class));
    }
}
