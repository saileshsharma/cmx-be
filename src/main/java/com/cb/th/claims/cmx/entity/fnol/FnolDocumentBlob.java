package com.cb.th.claims.cmx.entity.fnol;

import jakarta.persistence.*;

@Entity
@Table(name = "fnol_document_blob")
public class FnolDocumentBlob {

    @Id
    @Column(name = "document_id")
    private Long documentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private FnolDocument document;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    // getters/setters …
}