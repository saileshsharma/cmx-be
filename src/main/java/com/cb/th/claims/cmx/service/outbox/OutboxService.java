package com.cb.th.claims.cmx.service.outbox;
import com.cb.th.claims.cmx.entity.outbox.EventOutbox;
import com.cb.th.claims.cmx.repository.outbox.EventOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final EventOutboxRepository repo;
    private final ObjectMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(String topic, String key, Object payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            repo.save(EventOutbox.builder()
                    .topic(topic)
                    .messageKey(key)
                    .payloadJson(json)
                    .payloadType(payload.getClass().getName())
                    .attempts(0)
                    .createdAt(LocalDateTime.now())
                    .build());
        } catch (JsonProcessingException e) {
            // Last resort: still persist with error note
            repo.save(EventOutbox.builder()
                    .topic(topic)
                    .messageKey(key)
                    .payloadJson("{}")
                    .payloadType(payload.getClass().getName())
                    .attempts(0)
                    .createdAt(LocalDateTime.now())
                    .lastError("serialize:" + e.getMessage())
                    .build());
        }
    }
}