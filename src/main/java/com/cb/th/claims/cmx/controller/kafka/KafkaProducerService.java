package com.cb.th.claims.cmx.controller.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

/*    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<RecordMetadata> send(String topic, String key, String value) {
        // send(...) returns CompletableFuture<SendResult<K,V>> in Spring Kafka 3.x
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, value);
        return future.thenApply(SendResult::getRecordMetadata);
    }*/
}