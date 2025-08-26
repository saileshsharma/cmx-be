package com.cb.th.claims.cmx.entity.fnol;

import com.cb.th.claims.cmx.enums.fnol.DocumentCategory;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "fnol_required_doc_status",
        uniqueConstraints = @UniqueConstraint(name="uk_fnol_doccat", columnNames={"fnol_id","doc_category"}),
        indexes = @Index(name="ix_fnol_req_status_fnol", columnList="fnol_id"))
public class FnolRequiredDocStatus {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Replace Fnol with your FNOL entity
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fnol_id", nullable = false)
    private FNOL fnol;

    @Column(name = "policy_type", nullable = false, length = 64)
    private String policyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_category", nullable = false, length = 64)
    private DocumentCategory docCategory;

    @Column(name = "required_count", nullable = false)
    private int requiredCount = 1;

    @Column(name = "uploaded_count", nullable = false)
    private int uploadedCount = 0;

    /** Stored generated column in DB; read‑only to JPA. */
    @Column(name = "met", insertable = false, updatable = false)
    private Boolean met;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // getters/setters …
}