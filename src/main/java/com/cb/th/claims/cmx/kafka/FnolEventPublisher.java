package com.cb.th.claims.cmx.kafka;


import com.cb.th.claims.cmx.events.FnolCreated;

public interface FnolEventPublisher {
    void publishCreated(FnolCreated event);

    void publishUpdated(FnolCreated event); // or a different event type if you have one
}



