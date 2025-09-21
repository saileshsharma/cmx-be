# Database Schema & Entity Relationships

## Entity Relationship Diagram

```mermaid
erDiagram
    POLICY ||--|| VEHICLE : "covers"
    POLICY ||--o{ FNOL : "has claims"
    POLICY ||--|| INSURED : "belongs to"
    FNOL ||--|| ADDRESS : "occurred at"
    FNOL ||--o| SURVEYOR : "assigned to"
    FNOL ||--o{ CLAIM : "generates"
    CLAIM ||--|| POLICY : "references"
    CLAIM ||--o| SURVEYOR : "assigned to"
    CLAIM ||--o| ADDRESS : "related to"
    INSURED ||--|| ADDRESS : "resides at"
    FNOL ||--o{ FNOL_DETAIL : "has details"
    FNOL ||--o{ FNOL_DOCUMENT : "has documents"
    FNOL_DOCUMENT ||--o| FNOL_DOCUMENT_BLOB : "has blob"
    FNOL_DOCUMENT ||--o| FNOL_DOCUMENT_THUMBNAIL : "has thumbnail"
    FNOL_DOCUMENT ||--o| FNOL_DOCUMENT_IMAGE_EXIF : "has exif"

    POLICY {
        bigint id PK
        varchar policy_number UK "Business identifier"
        varchar coverage_type
        enum policy_status "ACTIVE, LAPSED, CANCELLED, EXPIRED"
        varchar lob "Line of Business"
        date start_date
        date end_date
        varchar policy_type
        decimal sum_insured
        decimal premium
        bigint insured_id FK
        bigint vehicle_id FK
        date issue_date
        date renewal_date
        date cancel_date
        date lapse_date
        enum payment_frequency
        enum payment_method
    }

    VEHICLE {
        bigint id PK
        varchar vin "Vehicle Identification Number"
        varchar registration_number UK "License plate"
        varchar make
        varchar model
        integer year
        varchar chassis
        varchar color
        varchar body_type
        varchar engine_no
        varchar fuel_type
        varchar usage_type
        varchar owner_name
        varchar owner_contact
        varchar registration_state
        timestamp created_at
        timestamp updated_at
    }

    INSURED {
        bigint id PK
        varchar first_name
        varchar last_name
        date dob
        varchar gender
        varchar national_id
        varchar passport_number
        varchar email
        varchar phone_number
        varchar address_line1
        varchar address_line2
        varchar city
        varchar province
        varchar postal_code
        varchar country
        varchar driver_license_no
        date license_issue_date
        date license_expiry_date
        varchar occupation
        varchar marital_status
        integer years_driving
        bigint address_id FK
        timestamp created_at
        timestamp updated_at
    }

    FNOL {
        bigint id PK
        varchar fnol_reference_no UK "TH-AT-FN-######"
        varchar incident_description
        varchar description
        enum severity "HIGH, MEDIUM, LOW"
        timestamp accident_date
        enum fnol_state "DRAFT, SUBMITTED, ENRICHING, VALIDATED, REJECTED"
        bigint policy_id FK
        bigint vehicle_id FK
        bigint surveyor_id FK "nullable"
        bigint accident_location_id FK
        date updated_at
    }

    CLAIM {
        bigint id PK
        varchar claim_number UK "TH-AT-CL-######"
        enum status "REGISTERED, UNDER_REVIEW, APPROVED, etc."
        decimal claim_amount
        bigint fnol_id FK
        bigint policy_id FK
        enum claim_status
        date incident_date
        date claim_date
        date date_reported
        enum claim_severity
        varchar location
        bigint surveyor_id FK
        bigint address_id FK
    }

    SURVEYOR {
        bigint id PK
        varchar name
        varchar email
        varchar phone_number
        decimal current_lat "Current latitude"
        decimal current_lng "Current longitude"
        integer rating_avg
        varchar app_version
        varchar capacity_per_day
        integer active_jobs_count
        varchar skills
        varchar city
        varchar province
        varchar country
        boolean is_active
        boolean internal
        enum status "AVAILABLE, UNAVAILABLE, INACTIVE"
        enum surveyor_job_status
        timestamp created_at
        timestamp updated_at
    }

    ADDRESS {
        bigint id PK
        varchar address_line1
        varchar address_line2
        varchar city
        varchar province
        varchar postal_code
        varchar country
        decimal latitude "Google Maps Lat"
        decimal longitude "Google Maps Lng"
        varchar google_place_id
        enum location_type
        timestamp created_at
        date updated_at
    }

    FNOL_DETAIL {
        bigint id PK
        bigint fnol_id FK
        text additional_info
        timestamp created_at
    }

    FNOL_DOCUMENT {
        bigint id PK
        bigint fnol_id FK
        varchar document_name
        varchar document_type
        varchar file_path
        bigint file_size
        varchar mime_type
        enum document_status
        timestamp uploaded_at
        varchar uploaded_by
    }

    FNOL_DOCUMENT_BLOB {
        bigint id PK
        bigint fnol_document_id FK
        bytea blob_data
    }

    FNOL_DOCUMENT_THUMBNAIL {
        bigint id PK
        bigint fnol_document_id FK
        bytea thumbnail_data
        varchar thumbnail_format
    }

    FNOL_DOCUMENT_IMAGE_EXIF {
        bigint id PK
        bigint fnol_document_id FK
        json exif_data
    }
```

## Database Constraints & Indexes

### Primary Keys
- All tables use `BIGINT` auto-increment primary keys
- Generated using `IDENTITY` strategy

### Unique Constraints
```sql
-- Business identifiers must be unique
ALTER TABLE policy ADD CONSTRAINT uk_policy_number UNIQUE (policy_number);
ALTER TABLE fnol ADD CONSTRAINT uk_fnol_reference_no UNIQUE (fnol_reference_no);
ALTER TABLE claim ADD CONSTRAINT uk_claim_number UNIQUE (claim_number);
ALTER TABLE vehicle ADD CONSTRAINT uk_vehicle_registration UNIQUE (registration_number);
```

### Foreign Key Constraints
```sql
-- Policy relationships
ALTER TABLE policy ADD CONSTRAINT fk_policy_insured
    FOREIGN KEY (insured_id) REFERENCES insured(id);
ALTER TABLE policy ADD CONSTRAINT fk_policy_vehicle
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id);

-- FNOL relationships
ALTER TABLE fnol ADD CONSTRAINT fk_fnol_policy
    FOREIGN KEY (policy_id) REFERENCES policy(id);
ALTER TABLE fnol ADD CONSTRAINT fk_fnol_vehicle
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id);
ALTER TABLE fnol ADD CONSTRAINT fk_fnol_surveyor
    FOREIGN KEY (surveyor_id) REFERENCES surveyor(id);
ALTER TABLE fnol ADD CONSTRAINT fk_fnol_accident_location
    FOREIGN KEY (accident_location_id) REFERENCES address(id);

-- Claim relationships
ALTER TABLE claim ADD CONSTRAINT fk_claim_fnol
    FOREIGN KEY (fnol_id) REFERENCES fnol(id);
ALTER TABLE claim ADD CONSTRAINT fk_claim_policy
    FOREIGN KEY (policy_id) REFERENCES policy(id);
ALTER TABLE claim ADD CONSTRAINT fk_claim_surveyor
    FOREIGN KEY (surveyor_id) REFERENCES surveyor(id);
ALTER TABLE claim ADD CONSTRAINT fk_claim_address
    FOREIGN KEY (address_id) REFERENCES address(id);
```

### Check Constraints
```sql
-- Enum validations
ALTER TABLE policy ADD CONSTRAINT ck_policy_status
    CHECK (policy_status IN ('ACTIVE', 'LAPSED', 'CANCELLED', 'EXPIRED'));

ALTER TABLE fnol ADD CONSTRAINT ck_fnol_state
    CHECK (fnol_state IN ('DRAFT', 'SUBMITTED', 'ENRICHING', 'VALIDATED', 'REJECTED'));

ALTER TABLE claim ADD CONSTRAINT ck_claim_severity
    CHECK (claim_severity IN ('HIGH', 'MEDIUM', 'LOW'));

ALTER TABLE surveyor ADD CONSTRAINT ck_surveyor_status
    CHECK (status IN ('AVAILABLE', 'UNAVAILABLE', 'INACTIVE'));

-- Business rules
ALTER TABLE insured ADD CONSTRAINT ck_insured_years_driving
    CHECK (years_driving >= 0);

ALTER TABLE vehicle ADD CONSTRAINT ck_vehicle_year
    CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 1);
```

### Indexes for Performance

#### Business Identifier Indexes
```sql
-- Already covered by unique constraints
-- uk_policy_number, uk_fnol_reference_no, uk_claim_number, uk_vehicle_registration
```

#### Foreign Key Indexes
```sql
CREATE INDEX idx_fnol_policy_id ON fnol(policy_id);
CREATE INDEX idx_fnol_vehicle_id ON fnol(vehicle_id);
CREATE INDEX idx_fnol_surveyor_id ON fnol(surveyor_id);
CREATE INDEX idx_claim_fnol_id ON claim(fnol_id);
CREATE INDEX idx_claim_policy_id ON claim(policy_id);
CREATE INDEX idx_claim_surveyor_id ON claim(surveyor_id);
```

#### Query Optimization Indexes
```sql
-- FNOL state queries
CREATE INDEX idx_fnol_state ON fnol(fnol_state);

-- Active policy queries
CREATE INDEX idx_policy_active ON policy(policy_status)
    WHERE policy_status = 'ACTIVE';

-- Surveyor availability queries
CREATE INDEX idx_surveyor_available ON surveyor(status, is_active)
    WHERE status = 'AVAILABLE' AND is_active = true;

-- Date range queries
CREATE INDEX idx_fnol_accident_date ON fnol(accident_date);
CREATE INDEX idx_claim_date_reported ON claim(date_reported);
```

#### Spatial Indexes (PostGIS)
```sql
-- Geographic queries for surveyor assignment
CREATE INDEX idx_address_location ON address
    USING GIST (ST_Point(longitude, latitude));

CREATE INDEX idx_surveyor_location ON surveyor
    USING GIST (ST_Point(current_lng, current_lat))
    WHERE current_lat IS NOT NULL AND current_lng IS NOT NULL;
```

## Sequence Generators

### Business Number Generation
```sql
-- FNOL reference number sequence
CREATE SEQUENCE fnol_no_seq START 1;

-- Claim number sequence
CREATE SEQUENCE claim_no_seq START 1;

-- Database function for formatted FNOL reference
CREATE OR REPLACE FUNCTION next_fnol_reference()
RETURNS VARCHAR AS $$
BEGIN
    RETURN 'TH-AT-FN-' || LPAD(nextval('fnol_no_seq')::TEXT, 6, '0');
END;
$$ LANGUAGE plpgsql;

-- Database function for formatted claim number
CREATE OR REPLACE FUNCTION next_claim_number()
RETURNS VARCHAR AS $$
BEGIN
    RETURN 'TH-AT-CL-' || LPAD(nextval('claim_no_seq')::TEXT, 6, '0');
END;
$$ LANGUAGE plpgsql;
```

## Data Types & Enums

### FNOL State Machine
```
DRAFT → SUBMITTED → ENRICHING → VALIDATED
                                     ↓
                                REJECTED
```

### Claim Status Lifecycle
```
REGISTERED → UNDER_REVIEW → APPROVAL_PENDING → APPROVED → WORK_IN_PROGRESS → PAID → CLOSED
                    ↓              ↓                                ↓
                REJECTED       REJECTED                        REOPENED
```

### Surveyor Status
- **AVAILABLE**: Ready for new assignments
- **UNAVAILABLE**: Temporarily not available
- **INACTIVE**: Account disabled

## Audit & Timestamps

### Audit Columns
All business tables include:
- `created_at`: Timestamp of record creation
- `updated_at`: Timestamp of last modification
- `created_by`: User who created the record (planned)
- `updated_by`: User who last modified the record (planned)

### Temporal Data
- Policy effective dates: `start_date`, `end_date`
- Claim reporting dates: `incident_date`, `claim_date`, `date_reported`
- License validity: `license_issue_date`, `license_expiry_date`

## Partitioning Strategy (Future)

### Time-based Partitioning
```sql
-- Partition FNOL by accident_date (monthly)
CREATE TABLE fnol_y2024m01 PARTITION OF fnol
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- Partition claims by date_reported (quarterly)
CREATE TABLE claim_q1_2024 PARTITION OF claim
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');
```

### Geographic Partitioning
```sql
-- Partition by province/region for large datasets
CREATE TABLE surveyor_bangkok PARTITION OF surveyor
    FOR VALUES IN ('Bangkok', 'Nonthaburi', 'Pathum Thani');
```