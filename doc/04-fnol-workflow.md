# FNOL (First Notice of Loss) Workflow

## FNOL State Machine

```mermaid
stateDiagram-v2
    [*] --> DRAFT : User starts FNOL
    DRAFT --> SUBMITTED : User submits form
    DRAFT --> [*] : User abandons

    SUBMITTED --> ENRICHING : Auto validation passed
    SUBMITTED --> REJECTED : Validation failed

    ENRICHING --> VALIDATED : Policy verified & eligible
    ENRICHING --> REJECTED : Policy invalid/expired

    VALIDATED --> DISPATCHED : Surveyor assigned
    VALIDATED --> COMPLETED : No survey needed
    VALIDATED --> REJECTED : Assignment failed

    DISPATCHED --> COMPLETED : Survey completed
    DISPATCHED --> REJECTED : Survey failed

    REJECTED --> [*] : Process ends
    COMPLETED --> [*] : Process ends

    note right of DRAFT
        - UI form saved
        - Minimal validation
        - Can be edited
    end note

    note right of SUBMITTED
        - Form submitted
        - Basic validation passed
        - Cannot be edited
    end note

    note right of ENRICHING
        - Policy lookup
        - Coverage verification
        - Fraud detection
        - Vehicle validation
    end note

    note right of VALIDATED
        - Eligibility confirmed
        - Claim number reserved
        - Ready for assignment
    end note

    note right of DISPATCHED
        - Surveyor assigned
        - Survey scheduled
        - Notifications sent
    end note
```

## Complete FNOL Process Flow

```mermaid
flowchart TD
    A[User Initiates FNOL] --> B{Policy Number Valid?}
    B -->|No| C[Show Error - Invalid Policy]
    B -->|Yes| D[Load Policy Details]

    D --> E{Policy Active?}
    E -->|No| F[Show Error - Policy Expired/Cancelled]
    E -->|Yes| G[Validate Vehicle Registration]

    G --> H{Vehicle Covered?}
    H -->|No| I[Show Error - Vehicle Not Covered]
    H -->|Yes| J[User Fills Accident Details]

    J --> K[Select Accident Location]
    K --> L[Upload Documents/Photos]
    L --> M[Review & Submit FNOL]

    M --> N[FNOL State: SUBMITTED]
    N --> O[Trigger Background Processing]

    O --> P[Policy Validation Service]
    P --> Q{Policy Eligible?}
    Q -->|No| R[FNOL State: REJECTED]
    Q -->|Yes| S[Coverage Verification]

    S --> T{Coverage Applicable?}
    T -->|No| R
    T -->|Yes| U[Fraud Detection Checks]

    U --> V{Fraud Detected?}
    V -->|Yes| R
    V -->|No| W[FNOL State: VALIDATED]

    W --> X[Generate Claim Number]
    X --> Y[Determine Survey Requirement]

    Y --> Z{Survey Required?}
    Z -->|No| AA[FNOL State: COMPLETED]
    Z -->|Yes| BB[Find Available Surveyor]

    BB --> CC{Surveyor Found?}
    CC -->|No| DD[Queue for Assignment]
    CC -->|Yes| EE[Assign Surveyor]

    EE --> FF[FNOL State: DISPATCHED]
    FF --> GG[Send Notifications]
    GG --> HH[Surveyor App Updates]

    DD --> II[Retry Assignment Process]
    II --> CC

    R --> JJ[Send Rejection Notification]
    AA --> KK[Auto-Create Simple Claim]
    FF --> LL[Wait for Survey Completion]

    LL --> MM{Survey Completed?}
    MM -->|Yes| NN[FNOL State: COMPLETED]
    MM -->|Failed| OO[FNOL State: REJECTED]

    NN --> PP[Process Survey Results]
    PP --> QQ[Update Claim Details]

    style A fill:#e1f5fe
    style N fill:#fff3e0
    style W fill:#e8f5e8
    style R fill:#ffebee
    style AA fill:#e8f5e8
    style FF fill:#f3e5f5
```

## FNOL Business Rules

### Validation Rules

```mermaid
graph TB
    subgraph "Input Validation"
        A1[Policy Number Format] --> A2[TH-POL-YYYYMMDD-XXXX]
        B1[Registration Format] --> B2[Thai License Plate Rules]
        C1[Accident Date] --> C2[Not Future Date]
        C1 --> C3[Within Reporting Window]
        D1[Required Fields] --> D2[Description, Location, Severity]
    end

    subgraph "Business Validation"
        E1[Policy Status] --> E2[Must be ACTIVE]
        F1[Policy Dates] --> F2[Accident within coverage period]
        G1[Vehicle Coverage] --> G2[Vehicle listed on policy]
        H1[Duplicate Check] --> H2[No open FNOL for same vehicle]
        I1[Fraud Indicators] --> I2[Multiple claims pattern]
        I1 --> I3[Suspicious location/time]
    end

    subgraph "Eligibility Rules"
        J1[Coverage Type] --> J2[Accident covered by policy type]
        K1[Deductible] --> K2[Claim amount vs deductible]
        L1[Geographic Limits] --> L2[Accident within covered area]
        M1[Time Limits] --> M2[Reported within time limit]
    end
```

### Assignment Rules

```mermaid
graph TB
    subgraph "Surveyor Selection Criteria"
        A1[Location Proximity] --> A2[Within 50km radius]
        B1[Availability Status] --> B2[Status = AVAILABLE]
        C1[Skill Match] --> C2[Vehicle type expertise]
        D1[Workload] --> D2[Current capacity check]
        E1[Rating] --> E2[Minimum rating threshold]
    end

    subgraph "Assignment Algorithm"
        F1[Distance Calculation] --> F2[Haversine formula]
        G1[Load Balancing] --> G2[Least assigned first]
        H1[Skill Weighting] --> H2[Premium for specialization]
        I1[Emergency Priority] --> I2[HIGH severity first]
    end

    subgraph "Fallback Strategy"
        J1[No Local Surveyor] --> J2[Expand search radius]
        K1[All Busy] --> K2[Queue for next available]
        L1[Assignment Timeout] --> L2[Manual assignment required]
    end
```

## Integration Points

### External System Interactions

```mermaid
sequenceDiagram
    participant UI as CMX-UI
    participant BE as CMX-BE
    participant Dispatcher as CMX-Dispatcher
    participant Uploader as CMX-Uploader
    participant Kafka as Kafka
    participant DB as PostgreSQL

    UI->>BE: createFnol(input)
    BE->>DB: Validate policy & vehicle
    DB-->>BE: Validation results

    alt Policy Valid
        BE->>DB: Save FNOL (SUBMITTED)
        BE->>Kafka: Publish FnolSubmitted event
        BE-->>UI: Success response

        Kafka->>BE: Process enrichment
        BE->>DB: Update FNOL (ENRICHING)
        BE->>BE: Run fraud checks
        BE->>BE: Verify coverage

        alt Validation Passed
            BE->>DB: Update FNOL (VALIDATED)
            BE->>Kafka: Publish FnolValidated event

            Kafka->>Dispatcher: Assignment request
            Dispatcher->>Dispatcher: Find surveyor
            Dispatcher->>Kafka: SurveyorAssigned event

            Kafka->>BE: Update assignment
            BE->>DB: Update FNOL (DISPATCHED)
            BE->>UI: Notify assignment (WebSocket)

        else Validation Failed
            BE->>DB: Update FNOL (REJECTED)
            BE->>UI: Notify rejection
        end

    else Policy Invalid
        BE-->>UI: Error response
    end

    Note over UI,DB: Document upload flow
    UI->>Uploader: Upload documents
    Uploader->>Kafka: DocumentUploaded event
    Kafka->>BE: Link documents to FNOL
    BE->>DB: Save document references
```

## Error Handling & Recovery

### Error Scenarios

```mermaid
graph TD
    A[FNOL Processing Error] --> B{Error Type}

    B -->|Validation Error| C[Business Rule Violation]
    B -->|System Error| D[Technical Failure]
    B -->|External Service Error| E[Integration Failure]

    C --> C1[Invalid Policy]
    C --> C2[Duplicate FNOL]
    C --> C3[Coverage Exclusion]
    C --> C4[Fraud Detection]

    C1 --> F[Set FNOL to REJECTED]
    C2 --> F
    C3 --> F
    C4 --> G[Flag for Manual Review]

    D --> D1[Database Connection]
    D --> D2[Application Error]
    D --> D3[Memory/Resource Issue]

    D1 --> H[Retry with Backoff]
    D2 --> I[Log Error & Alert]
    D3 --> I

    E --> E1[Kafka Connection]
    E --> E2[External API Timeout]
    E --> E3[Service Unavailable]

    E1 --> J[Queue for Retry]
    E2 --> K[Circuit Breaker]
    E3 --> K

    F --> L[Send User Notification]
    G --> M[Admin Dashboard Alert]
    H --> N{Retry Success?}
    N -->|Yes| O[Continue Processing]
    N -->|No| P[Manual Intervention]

    I --> Q[Operations Team Alert]
    J --> R[Dead Letter Queue]
    K --> S[Degrade Gracefully]
```

### Recovery Mechanisms

#### Idempotency
```java
@Service
public class FnolService {

    @Transactional
    @Idempotent(key = "#command.idempotencyKey")
    public CreateFnolPayload createFnol(CreateFnolCommand command) {
        // Check if FNOL already exists for this key
        if (fnolRepository.existsByIdempotencyKey(command.getIdempotencyKey())) {
            return fnolRepository.findByIdempotencyKey(command.getIdempotencyKey());
        }
        // Proceed with creation
    }
}
```

#### Retry Logic
```java
@Retryable(
    value = {DataAccessException.class, KafkaException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
public void publishFnolEvent(FnolEvent event) {
    kafkaTemplate.send("fnol-events", event);
}

@Recover
public void recoverFromPublishFailure(Exception ex, FnolEvent event) {
    // Store in dead letter queue or database for manual processing
    deadLetterService.store(event, ex);
}
```

## Performance Considerations

### Optimization Strategies

```mermaid
graph TB
    subgraph "Database Optimization"
        A1[Connection Pooling] --> A2[HikariCP Configuration]
        B1[Query Optimization] --> B2[Indexed Lookups]
        C1[Lazy Loading] --> C2[JPA Fetch Strategies]
        D1[Batch Processing] --> D2[Bulk Operations]
    end

    subgraph "Caching Strategy"
        E1[Policy Cache] --> E2[Redis TTL 1 hour]
        F1[Vehicle Cache] --> F2[Local Cache 15 min]
        G1[Surveyor Location] --> G2[Real-time updates]
        H1[Query Result Cache] --> H2[GraphQL field caching]
    end

    subgraph "Async Processing"
        I1[Event-Driven] --> I2[Kafka Async Processing]
        J1[Background Jobs] --> J2[Scheduled Tasks]
        K1[WebSocket Updates] --> K2[Real-time UI Updates]
    end
```

### Monitoring & Metrics

```mermaid
graph TB
    subgraph "Business Metrics"
        A1[FNOL Creation Rate] --> A2[Per minute/hour]
        B1[Processing Time] --> B2[State transition duration]
        C1[Success Rate] --> C2[VALIDATED vs REJECTED ratio]
        D1[Assignment Efficiency] --> D2[Time to surveyor assignment]
    end

    subgraph "Technical Metrics"
        E1[Response Time] --> E2[GraphQL query performance]
        F1[Error Rate] --> F2[System and business errors]
        G1[Throughput] --> G2[Requests per second]
        H1[Resource Usage] --> H2[CPU, Memory, DB connections]
    end

    subgraph "Alerting Thresholds"
        I1[Error Rate > 5%] --> I2[Page Operations Team]
        J1[Processing Time > 30s] --> J2[Investigate Performance]
        K1[Queue Depth > 100] --> K2[Scale Resources]
        L1[Assignment Failure > 10%] --> L2[Check Surveyor Availability]
    end
```