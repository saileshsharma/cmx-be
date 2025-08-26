package com.cb.th.claims.cmx.entity.fnol;

import jakarta.persistence.*;

@Entity
@Table(name = "fnol_document_image_exif")
public class FnolDocumentImageExif {

    @Id
    @Column(name = "document_id")
    private Long documentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private FnolDocument document;

    @Column(name = "width_px")
    private Integer widthPx;

    @Column(name = "height_px")
    private Integer heightPx;

    @Column(name = "orientation")
    private Short orientation;

    @Column(name = "camera_make")
    private String cameraMake;

    @Column(name = "camera_model")
    private String cameraModel;

    @Column(name = "iso")
    private Integer iso;

    @Column(name = "exposure_time")
    private String exposureTime;

    @Column(name = "f_number")
    private String fNumber;

    @Column(name = "focal_length_mm")
    private String focalLengthMm;

    @Column(name = "gps_latitude")
    private Double gpsLatitude;

    @Column(name = "gps_longitude")
    private Double gpsLongitude;

    @Column(name = "taken_at")
    private java.time.Instant takenAt;

    // getters/setters …
}