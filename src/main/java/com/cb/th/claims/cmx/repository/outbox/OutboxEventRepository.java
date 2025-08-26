package com.cb.th.claims.cmx.repository.outbox;


import com.cb.th.claims.cmx.events.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    @Query(value = """
              SELECT * FROM outbox_event
              WHERE published_at IS NULL
              ORDER BY id
              LIMIT :limit
            """, nativeQuery = true)
    List<OutboxEvent> findBatch(int limit);
}