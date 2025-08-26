package com.cb.th.claims.cmx.subscriptions;


import com.cb.th.claims.cmx.kafka.notice.FnolAssignmentNotice;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class FnolSubscription {

    private final NoticeBus bus;

    @SubscriptionMapping
    public Publisher<FnolAssignmentNotice> fnolAssignmentNotice(@Argument String fnolRef) {
        return bus.sink().asFlux().filter(n -> n.getFnolReferenceNo() != null && n.getFnolReferenceNo().equals(fnolRef));
    }
}
