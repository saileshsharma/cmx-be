package com.cb.th.claims.cmx.entity.fnol;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fnol_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FnolDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "additional_notes")
    private String additionalNotes;

    @Column(name = "other_party_involved")
    private String otherPartyInvolved;

    @Column(name = "supporting_document_url")
    private String supportingDocumentUrl;

    @Column(name = "witness_name")
    private String witnessName;

    @Column(name = "witness_phone")
    private String witnessPhone;

    @OneToOne
    @JoinColumn(name = "fnol_id", unique = true)
    private FNOL fnol;
}
