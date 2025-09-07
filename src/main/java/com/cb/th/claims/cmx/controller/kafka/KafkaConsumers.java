package com.cb.th.claims.cmx.controller.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumers {

    // Reads from the cmx.test topic using the group set in application.properties
    @KafkaListener(topics = "${cmx.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("✅ Received from {} partition={} offset={}: key={}, value={}", record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}



