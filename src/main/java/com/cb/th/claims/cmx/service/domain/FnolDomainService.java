package com.cb.th.claims.cmx.service.domain;

import com.cb.th.claims.cmx.events.FnolCreated;
import com.cb.th.claims.cmx.events.OutboxEvent;
import com.cb.th.claims.cmx.repository.outbox.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class FnolDomainService {
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper om;
    // private final FNOLRepository fnolRepo; // your existing repo

    @Transactional
    public void createFnolAndEmitEvent(/* your inputs */) {
        // 1) save FNOL entity (omitted)
        Long fnolDbId = /* saved id */ 123L;

        // 2) build event
        FnolCreated evt = FnolCreated.builder().fnolId(String.valueOf(fnolDbId)).policyNumber("P-001").registrationNumber("SGA1234Z").regionId("SG-CENTRAL").requiredSkills(java.util.List.of("MOTOR")).accident(FnolCreated.Accident.builder().lat(1.30).lng(103.80).severity("MINOR").time(java.time.Instant.now()).build()).meta(new FnolCreated.Meta("trace-" + fnolDbId)).build();

        // 3) insert outbox in same TX
        outboxRepo.save(OutboxEvent.builder().aggregateType("FNOL").aggregateId(String.valueOf(fnolDbId)).type("fnol.created").payload(serialize(om, evt)).createdAt(OffsetDateTime.now()).build());
    }

    private static String serialize(ObjectMapper om, Object o) {
        try {
            return om.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}