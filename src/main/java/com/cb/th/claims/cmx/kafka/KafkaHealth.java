package com.cb.th.claims.cmx.kafka;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KafkaHealth {
    private final AdminClient adminClient;

    public boolean isUp() {
        try {
            adminClient.describeCluster().nodes().get(1, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}