package com.cb.th.claims.cmx.entity.fnol;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "fnol_document_thumbnail",
        uniqueConstraints = @UniqueConstraint(name = "uk_doc_variant", columnNames = {"document_id","variant"}),
        indexes = @Index(name="ix_fnol_thumbnail_doc", columnList="document_id"))
public class FnolDocumentThumbnail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private FnolDocument document;

    @Column(name = "variant", nullable = false, length = 64)
    private String variant; // sm/md/lg/webp

    @Column(name = "width_px")
    private Integer widthPx;

    @Column(name = "height_px")
    private Integer heightPx;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;

    @Column(name = "storage_url", columnDefinition = "text")
    private String storageUrl;

    @Column(name = "storage_etag", length = 255)
    private String storageEtag;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // getters/setters …
}