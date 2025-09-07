package com.cb.th.claims.cmx.resolver.fnol;


import com.cb.th.claims.cmx.subscriptions.FnolAssignmentSink;
import com.cb.th.claims.cmx.subscriptions.dto.FnolAssignmentNoticeDto;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FnolSubscriptionResolver {

    private final FnolAssignmentSink sink;

    public Publisher<FnolAssignmentNoticeDto> fnolAssignmentNotice(String fnolReferenceNo) {
        return sink.sink().asFlux().filter(n -> fnolReferenceNo.equals(n.getFnolReferenceNo()));
    }
}