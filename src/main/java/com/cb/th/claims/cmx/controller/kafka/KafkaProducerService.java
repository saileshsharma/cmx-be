package com.cb.th.claims.cmx.controller.kafka;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<RecordMetadata> send(String topic, String key, String value) {
        // send(...) returns CompletableFuture<SendResult<K,V>> in Spring Kafka 3.x
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, value);
        return future.thenApply(SendResult::getRecordMetadata);
    }
}