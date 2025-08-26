package com.cb.th.claims.cmx.kafka;

import com.cb.th.claims.cmx.events.FnolCreated;
import com.cb.th.claims.cmx.service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaFnolEventPublisher implements FnolEventPublisher {

    private final KafkaTemplate<String, FnolCreated> kafkaTemplate;
    private final KafkaHealth kafkaHealth;   // AdminClient-based probe
    private final OutboxService outbox;      // DB outbox fallback

    @Value("${cmx.kafka.topic.fnol.created:fnol.created}")
    private String topicCreated;

    @Value("${cmx.kafka.topic.fnol.updated:fnol.updated}")
    private String topicUpdated;

    @Override
    public void publishCreated(FnolCreated event) {
        final String key = event.getFnolId();

        // If broker looks down, short-circuit to outbox
        if (!kafkaHealth.isUp()) {
            log.warn("Kafka DOWN; enqueueing to outbox. topic={}, key={}", topicCreated, key);
            outbox.save(topicCreated, key, event);
            return;
        }

        kafkaTemplate.send(topicCreated, key, event).whenComplete((SendResult<String, FnolCreated> res, Throwable ex) -> {
            if (ex != null) {
                log.error("Failed to publish FNOL created; enqueueing to outbox. topic={}, key={}, error={}", topicCreated, key, ex.toString(), ex);
                outbox.save(topicCreated, key, event);
                return;
            }
            RecordMetadata m = res.getRecordMetadata();
            log.info("Published FNOL created. topic={}, partition={}, offset={}, key={}", m.topic(), m.partition(), m.offset(), key);
        });
    }

    @Override
    public void publishUpdated(FnolCreated event) { // swap to FnolUpdated type when available
        final String key = event.getFnolId();

        if (!kafkaHealth.isUp()) {
            log.warn("Kafka DOWN; enqueueing to outbox. topic={}, key={}", topicUpdated, key);
            outbox.save(topicUpdated, key, event);
            return;
        }

        kafkaTemplate.send(topicUpdated, key, event).whenComplete((SendResult<String, FnolCreated> res, Throwable ex) -> {
            if (ex != null) {
                log.error("Failed to publish FNOL updated; enqueueing to outbox. topic={}, key={}, error={}", topicUpdated, key, ex.toString(), ex);
                outbox.save(topicUpdated, key, event);
                return;
            }
            RecordMetadata m = res.getRecordMetadata();
            log.info("Published FNOL updated. topic={}, partition={}, offset={}, key={}", m.topic(), m.partition(), m.offset(), key);
        });
    }
}
