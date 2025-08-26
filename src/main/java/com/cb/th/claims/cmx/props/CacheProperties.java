package com.cb.th.claims.cmx.props;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "cmx.cache")
public class CacheProperties {
    /**
     * Map of cacheName -> Caffeine spec string
     * e.g. cmx.cache.specs.policyByNumber=maximumSize=10_000,expireAfterWrite=10m
     */
    private Map<String, String> specs = new HashMap<>();

    public Map<String, String> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, String> specs) {
        this.specs = specs;
    }
}