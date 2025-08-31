package com.cb.th.claims.cmx.config.cache;

import com.cb.th.claims.cmx.props.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {
/*
    @Bean
    public CacheManager cacheManager(CacheProperties props) {
        Map<String,String> specs = props.getSpecs();

        List<CaffeineCache> caches = new ArrayList<>();
        if (specs == null || specs.isEmpty()) {
            // Safe fallback: create a single default cache so the app still boots
            caches.add(new CaffeineCache(
                    "default",
                    Caffeine.newBuilder()
                            .maximumSize(10_000)
                            .expireAfterWrite(Duration.ofMinutes(5))
                            .recordStats()
                            .build()
            ));
        } else {
            specs.forEach((name, spec) ->
                    caches.add(new CaffeineCache(name, Caffeine.from(spec).recordStats().build()))
            );
        }

        SimpleCacheManager mgr = new SimpleCacheManager();
        mgr.setCaches(caches);
        return mgr;
    }*/
}