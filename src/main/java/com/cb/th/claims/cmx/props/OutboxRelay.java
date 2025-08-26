package com.cb.th.claims.cmx.props;

import com.cb.th.claims.cmx.events.OutboxEvent;
import com.cb.th.claims.cmx.repository.outbox.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component("outboxRelayer")  // <--- distinct bean name, no clash
public class OutboxRelay {
    private final OutboxEventRepository repo;
    private final KafkaTemplate<String, Object> kafka;
    private final ObjectMapper om;
    private final OutboxProperties props;

    @Scheduled(fixedDelayString = "${outbox.relay.fixedDelay:1000}")
    @Transactional
    public void pump() {
        List<OutboxEvent> batch = repo.findBatch(props.getBatchSize());
        for (OutboxEvent e : batch) {
            try {
                Object payload = om.readTree(e.getPayload()); // keep as JsonNode
                String topic = e.getType();                   // type == topic (e.g., fnol.created)
                String key = e.getAggregateId();
                kafka.send(topic, key, payload).get();        // sync for simplicity
                e.setPublishedAt(OffsetDateTime.now());
            } catch (Exception ex) {
                log.error("Outbox publish failed id={}", e.getId(), ex);
            }
        }
    }
}