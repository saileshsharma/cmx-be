package com.cb.th.claims.cmx.entity.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;
    @Column(nullable = false, name = "message_key")
    private String messageKey;

    @Column(nullable = false, columnDefinition = "text")
    private String payloadJson;

    @Column(nullable = false, name = "payload_type")
    private String payloadType; // FQCN (for deserialization)

    @Column(nullable = false)
    private Integer attempts;
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;
}