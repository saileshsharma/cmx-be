package com.cb.th.claims.cmx.service.outbox;

import com.cb.th.claims.cmx.entity.outbox.EventOutbox;
import com.cb.th.claims.cmx.kafka.KafkaHealth;
import com.cb.th.claims.cmx.repository.outbox.EventOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final EventOutboxRepository repo;
    private final KafkaTemplate<String, Object> genericKafkaTemplate;
    private final KafkaHealth health;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelayString = "${events.outbox.relay-interval-ms:5000}")
    public void flush() {
        if (!health.isUp()) return;

        List<EventOutbox> batch = repo.findTop100ByAttemptsLessThanOrderByCreatedAtAsc(10);
        for (EventOutbox rec : batch) {
            try {
                Class<?> type = Class.forName(rec.getPayloadType());
                Object obj = mapper.readValue(rec.getPayloadJson(), type);
                // Block shortly to know success; tune timeout if needed
                genericKafkaTemplate.send(rec.getTopic(), rec.getMessageKey(), obj)
                        .get(2, TimeUnit.SECONDS);
                repo.delete(rec);
            } catch (Exception ex) {
                rec.setAttempts(rec.getAttempts() + 1);
                rec.setLastError(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                repo.save(rec);
            }
        }
    }
}