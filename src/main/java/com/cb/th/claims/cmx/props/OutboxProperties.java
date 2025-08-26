package com.cb.th.claims.cmx.props;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component // bean name becomes "outboxProperties"
@ConfigurationProperties(prefix = "events.outbox")
public class OutboxProperties {
    private long relayIntervalMs = 5000;
    private int maxAttempts = 10;
    private int batchSize = 100;

    // getters/setters
    public long getRelayIntervalMs() {
        return relayIntervalMs;
    }

    public void setRelayIntervalMs(long v) {
        this.relayIntervalMs = v;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int v) {
        this.maxAttempts = v;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int v) {
        this.batchSize = v;
    }
}