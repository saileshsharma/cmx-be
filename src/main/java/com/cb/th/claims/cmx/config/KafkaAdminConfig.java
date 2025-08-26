package com.cb.th.claims.cmx.config;


import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaAdminConfig {

    @Bean
    public AdminClient kafkaAdminClient(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG, "1000");
        props.put(AdminClientConfig.RETRIES_CONFIG, "0");
        return AdminClient.create(props);
    }
}