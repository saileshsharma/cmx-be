package com.cb.th.claims.cmx.repository.outbox;

import com.cb.th.claims.cmx.entity.outbox.EventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventOutboxRepository extends JpaRepository<EventOutbox, Long> {
    List<EventOutbox> findTop100ByAttemptsLessThanOrderByCreatedAtAsc(int maxAttempts);
}