package com.cb.th.claims.cmx.subscriptions;

import com.cb.th.claims.cmx.subscriptions.dto.FnolAssignmentNoticeDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class FnolAssignmentSink {

    private final Sinks.Many<FnolAssignmentNoticeDto> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void emit(FnolAssignmentNoticeDto notice) {
        sink.tryEmitNext(notice);
    }

    public Sinks.Many<FnolAssignmentNoticeDto> sink() {
        return sink;
    }
}
