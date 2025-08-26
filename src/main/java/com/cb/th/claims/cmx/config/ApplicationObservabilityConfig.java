package com.cb.th.claims.cmx.config;


import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationObservabilityConfig {

    @Autowired
    private MeterRegistry meterRegistry;

    @PostConstruct
    public void initMetrics() {
        meterRegistry.config().commonTags("application", "cmx-be");
    }

}
