package com.cb.th.claims.cmx.listeners;


import com.cb.th.claims.cmx.events.FnolCreated;
import com.cb.th.claims.cmx.kafka.notice.FnolAssignmentNotice;
import com.cb.th.claims.cmx.subscriptions.NoticeBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FnolCreatedListener {

    private final NoticeBus bus;

    @KafkaListener(topics = "${cmx.topics.fnol-created:fnol.created}")
    public void onFnolCreated(FnolCreated event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Kafka {} -> FNOL created: id={}, policy={}, reg={}", topic, event.getFnolId(), event.getPolicyNumber(), event.getRegistrationNumber());

        // Bridge to UI (temporary): emit notice “sent to surveyor”
        var notice = FnolAssignmentNotice.builder().fnolReferenceNo(String.valueOf(event.getFnolId())) // or event.getFnolRef() if you have one
                .status("SENT_TO_SURVEYOR").message("FNOL has been sent to surveyor").timestamp(OffsetDateTime.now()).build();

        bus.publish(notice);
    }
}