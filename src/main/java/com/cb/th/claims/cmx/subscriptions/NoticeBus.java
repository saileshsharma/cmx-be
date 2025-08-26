package com.cb.th.claims.cmx.subscriptions;


import com.cb.th.claims.cmx.kafka.notice.FnolAssignmentNotice;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class NoticeBus {
    private final Sinks.Many<FnolAssignmentNotice> sink = Sinks.many().multicast().directBestEffort();

    public void publish(FnolAssignmentNotice notice) {
        sink.tryEmitNext(notice);
    }

    public Sinks.Many<FnolAssignmentNotice> sink() {
        return sink;
    }
}
