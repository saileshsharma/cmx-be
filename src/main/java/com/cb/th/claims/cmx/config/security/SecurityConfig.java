package com.cb.th.claims.cmx.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.oauth2.enabled:false}") // flip to true when you want JWT auth enforced
    private boolean oauthEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Respect spring.graphql.cors.* and general CORS config
                .cors(Customizer.withDefaults())

                // Allow cookie POSTs to /graphql etc. without CSRF blocking
                .csrf(csrf -> csrf.ignoringRequestMatchers("/graphql", "/graphiql/**", "/actuator/**"))

                .authorizeHttpRequests(auth -> {
                    // Public endpoints
                    auth
                            // GraphQL HTTP + WS handshake
                            .requestMatchers(HttpMethod.POST, "/graphql").permitAll().requestMatchers(HttpMethod.GET, "/graphql").permitAll()
                            // Dev tooling
                            .requestMatchers("/graphiql/**").permitAll()
                            // Monitoring
                            .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll();

                    // Fallback rule depends on oauthEnabled
                    if (oauthEnabled) {
                        auth.anyRequest().authenticated();
                    } else {
                        auth.anyRequest().permitAll();
                    }
                })

                // Useful if you embed consoles/frames (e.g., H2/graphiql) on same origin
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));

        if (oauthEnabled) {
            // Enforce JWT resource server when enabled
            http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        } else {
            // No auth flows in local/dev
            http.httpBasic(b -> {
            }).formLogin(f -> f.disable());
        }

        return http.build();
    }
}
