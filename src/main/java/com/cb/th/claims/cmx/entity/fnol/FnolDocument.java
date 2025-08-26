package com.cb.th.claims.cmx.entity.fnol;


import com.cb.th.claims.cmx.util.StringListJsonConverter;
import com.cb.th.claims.cmx.enums.fnol.DocumentCategory;
import com.cb.th.claims.cmx.enums.fnol.DocumentStatus;
import com.cb.th.claims.cmx.enums.fnol.FileKind;
import com.cb.th.claims.cmx.enums.fnol.StorageProvider;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fnol_document", uniqueConstraints = @UniqueConstraint(name = "ux_fnol_document_fnol_cat_ver", columnNames = {"fnol_id", "doc_category", "version"}), indexes = {@Index(name = "ix_fnol_document_fnol", columnList = "fnol_id"), @Index(name = "ix_fnol_document_category", columnList = "doc_category"), @Index(name = "ix_fnol_document_status", columnList = "status"), @Index(name = "ix_fnol_document_sha256", columnList = "sha256_hex")})
public class FnolDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOTE: Replace Fnol with your actual FNOL entity class
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fnol_id", nullable = false)
    private FNOL fnol;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_category", nullable = false, length = 64)
    private DocumentCategory docCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_kind", nullable = false, length = 16)
    private FileKind fileKind = FileKind.OTHER;

    @Column(name = "filename_original", nullable = false, length = 512)
    private String filenameOriginal;

    @Column(name = "content_type", nullable = false, length = 255)
    private String contentType;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "sha256_hex", nullable = false, length = 64)
    private String sha256Hex;

    @Column(name = "description")
    private String description;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DocumentStatus status = DocumentStatus.UPLOADED;

    /**
     * Business version (not JPA optimistic locking).
     */
    @Column(name = "version", nullable = false)
    private int businessVersion = 1;

    @Column(name = "uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant uploadedAt;

    @Column(name = "reviewed_by_user_id")
    private Long reviewedByUserId;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_comment")
    private String reviewComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", nullable = false, length = 32)
    private StorageProvider storageProvider = StorageProvider.AZURE_BLOB;

    @Column(name = "storage_account", length = 255)
    private String storageAccount;

    @Column(name = "storage_container", length = 255)
    private String storageContainer;

    @Column(name = "storage_region", length = 64)
    private String storageRegion;

    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;              // blob/object key

    @Column(name = "storage_url", columnDefinition = "text")
    private String storageUrl;

    @Column(name = "storage_etag", length = 255)
    private String storageEtag;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Relations
    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FnolDocumentImageExif imageExif;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FnolDocumentThumbnail> thumbnails = new ArrayList<>();

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FnolDocumentBlob blob; // optional if storing payload in DB

    // getters/setters …
    // (omitted for brevity — generate with Lombok or your IDE)
}