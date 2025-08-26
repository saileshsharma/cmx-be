package com.cb.th.claims.cmx.events;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aggregateType;
    private String aggregateId;
    private String type;
    @Column(columnDefinition = "jsonb")
    private String payload;
    @Column(columnDefinition = "jsonb")
    private String headers;
    private OffsetDateTime createdAt;
    private OffsetDateTime publishedAt;
}
