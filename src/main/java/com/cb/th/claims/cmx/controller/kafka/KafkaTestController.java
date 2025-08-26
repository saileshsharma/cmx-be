package com.cb.th.claims.cmx.controller.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka-test")
@RequiredArgsConstructor
public class KafkaTestController {

/*    private final KafkaProducerService producer;

    @Value("${cmx.kafka.topic}")
    private String topic;

    @PostMapping
    public CompletableFuture<String> send(@RequestParam(defaultValue = "key-1") String key, @RequestBody String payload) {
        return producer.send(topic, key, payload).thenApply(md -> String.format("Sent to %s-%d@%d", md.topic(), md.partition(), md.offset()));
    }*/
}