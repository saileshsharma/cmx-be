# Sequence Diagrams - Key Operations

## 1. FNOL Creation Flow

```mermaid
sequenceDiagram
    participant Client
    participant GraphQL as GraphQL Resolver
    participant FnolService as FNOL Service
    participant Validator as Domain Validator
    participant PolicyRepo as Policy Repository
    participant VehicleRepo as Vehicle Repository
    participant AddressRepo as Address Repository
    participant FnolRepo as FNOL Repository
    participant RefGen as Reference Generator
    participant KafkaProducer as Kafka Producer
    participant DB as PostgreSQL

    Client->>GraphQL: createFnol(CreateFnolInput)
    GraphQL->>GraphQL: Validate input parameters

    GraphQL->>FnolService: createFnol(CreateFnolCommand)
    FnolService->>FnolService: Parse accident date

    Note over FnolService,DB: Policy Validation
    FnolService->>PolicyRepo: findByPolicyNumber(policyNumber)
    PolicyRepo->>DB: SELECT * FROM policy WHERE policy_number = ?
    DB-->>PolicyRepo: Policy entity
    PolicyRepo-->>FnolService: Policy or empty

    alt Policy not found
        FnolService-->>GraphQL: PolicyNotFoundException
        GraphQL-->>Client: Error response
    else Policy found
        Note over FnolService,DB: Address Validation
        FnolService->>AddressRepo: findById(accidentLocationId)
        AddressRepo->>DB: SELECT * FROM address WHERE id = ?
        DB-->>AddressRepo: Address entity
        AddressRepo-->>FnolService: Address or empty

        alt Address not found
            FnolService-->>GraphQL: AddressNotFoundException
            GraphQL-->>Client: Error response
        else Address found
            Note over FnolService,DB: Vehicle Validation
            FnolService->>VehicleRepo: findByRegistrationNormalized(registration)
            VehicleRepo->>DB: SELECT * FROM vehicle WHERE registration_number = ?
            DB-->>VehicleRepo: Vehicle entity
            VehicleRepo-->>FnolService: Vehicle or empty

            alt Vehicle not found
                FnolService-->>GraphQL: VehicleNotFoundException
                GraphQL-->>Client: Error response
            else Vehicle found
                Note over FnolService,Validator: Business Rule Validation
                FnolService->>Validator: validatePolicyEligibility(policy, accidentDate)
                Validator->>Validator: Check policy status & dates
                alt Policy not eligible
                    Validator-->>FnolService: PolicyNotEligibleException
                    FnolService-->>GraphQL: Error response
                    GraphQL-->>Client: Error response
                else Policy eligible
                    FnolService->>Validator: validateVehicleBelongsToPolicy(vehicle, policy)
                    Validator->>Validator: Check vehicle-policy relationship
                    alt Vehicle not on policy
                        Validator-->>FnolService: VehicleNotOnPolicyException
                        FnolService-->>GraphQL: Error response
                        GraphQL-->>Client: Error response
                    else Vehicle valid
                        FnolService->>Validator: validateNoDuplicateOrOpenFnol(policy, registration, accidentDate)
                        Validator->>FnolRepo: Check for duplicate FNOLs
                        FnolRepo->>DB: SELECT COUNT(*) FROM fnol WHERE policy_id = ? AND...
                        DB-->>FnolRepo: Count
                        FnolRepo-->>Validator: Count result
                        alt Duplicate found
                            Validator-->>FnolService: DuplicateFnolException
                            FnolService-->>GraphQL: Error response
                            GraphQL-->>Client: Error response
                        else No duplicate
                            Note over FnolService,DB: FNOL Creation
                            FnolService->>RefGen: next()
                            RefGen->>DB: SELECT nextval('fnol_no_seq')
                            DB-->>RefGen: Next sequence number
                            RefGen-->>FnolService: Formatted reference number

                            FnolService->>FnolService: assembleFnol(policy, vehicle, address, command, accidentTs)
                            FnolService->>FnolRepo: save(fnol)
                            FnolRepo->>DB: INSERT INTO fnol (...)
                            DB-->>FnolRepo: FNOL with generated ID
                            FnolRepo-->>FnolService: Saved FNOL

                            Note over FnolService,KafkaProducer: Event Publishing
                            FnolService->>KafkaProducer: publishFnolCreated(fnol)
                            KafkaProducer->>KafkaProducer: Create CloudEvent
                            KafkaProducer->>KafkaProducer: Send to 'fnol-events' topic

                            FnolService-->>GraphQL: CreateFnolPayload(fnol, null)
                            GraphQL-->>Client: FnolResponseDto
                        end
                    end
                end
            end
        end
    end
```

## 2. Surveyor Assignment Flow

```mermaid
sequenceDiagram
    participant KafkaConsumer as Kafka Consumer
    participant AssignmentService as Assignment Service
    participant SurveyorRepo as Surveyor Repository
    participant FnolRepo as FNOL Repository
    participant GeoService as Geo Service
    participant DispatcherAPI as CMX-Dispatcher API
    participant KafkaProducer as Kafka Producer
    participant WebSocket as WebSocket Service
    participant DB as PostgreSQL

    Note over KafkaConsumer: FnolValidated event received
    KafkaConsumer->>AssignmentService: processFnolValidated(event)

    AssignmentService->>FnolRepo: findById(event.fnolId)
    FnolRepo->>DB: SELECT * FROM fnol WHERE id = ?
    DB-->>FnolRepo: FNOL entity
    FnolRepo-->>AssignmentService: FNOL

    AssignmentService->>AssignmentService: determineSurveyRequirement(fnol)
    alt Survey not required
        AssignmentService->>FnolRepo: updateFnolState(fnol, COMPLETED)
        FnolRepo->>DB: UPDATE fnol SET fnol_state = 'COMPLETED'
        AssignmentService->>KafkaProducer: publishFnolCompleted(fnol)
    else Survey required
        Note over AssignmentService,DB: Find Available Surveyors
        AssignmentService->>GeoService: getLocationFromAddress(fnol.accidentLocation)
        GeoService-->>AssignmentService: GeoPoint(lat, lng)

        AssignmentService->>SurveyorRepo: findAvailableNearLocation(lat, lng, radius)
        SurveyorRepo->>DB: SELECT * FROM surveyor WHERE status = 'AVAILABLE' AND ST_DWithin(location, point, radius)
        DB-->>SurveyorRepo: List of available surveyors
        SurveyorRepo-->>AssignmentService: Available surveyors

        alt No surveyors available
            AssignmentService->>AssignmentService: scheduleRetryAssignment(fnol, delay)
            AssignmentService->>KafkaProducer: publishAssignmentRetry(fnol, reason)
        else Surveyors available
            AssignmentService->>AssignmentService: selectBestSurveyor(surveyors, fnol)
            Note over AssignmentService: Ranking algorithm: distance + workload + skills

            AssignmentService->>DispatcherAPI: assignSurveyorToFnol(surveyorId, fnolId)
            DispatcherAPI-->>AssignmentService: AssignmentResult

            alt Assignment successful
                AssignmentService->>FnolRepo: updateFnolSurveyor(fnol, surveyor)
                FnolRepo->>DB: UPDATE fnol SET surveyor_id = ?, fnol_state = 'DISPATCHED'

                AssignmentService->>SurveyorRepo: updateSurveyorStatus(surveyor, BUSY)
                SurveyorRepo->>DB: UPDATE surveyor SET status = 'BUSY', active_jobs_count = active_jobs_count + 1

                AssignmentService->>KafkaProducer: publishSurveyorAssigned(fnol, surveyor)
                AssignmentService->>WebSocket: notifyFnolUpdate(fnol.id, 'DISPATCHED')
                AssignmentService->>WebSocket: notifySurveyorAssignment(surveyor.id, fnol)

            else Assignment failed
                AssignmentService->>AssignmentService: markSurveyorUnavailable(surveyor)
                AssignmentService->>AssignmentService: retryWithNextSurveyor(fnol, remainingSurveyors)
            end
        end
    end
```

## 3. Claim Creation Flow

```mermaid
sequenceDiagram
    participant Client
    participant GraphQL as GraphQL Resolver
    participant ClaimService as Claim Service
    participant FnolRepo as FNOL Repository
    participant ClaimRepo as Claim Repository
    participant RefGen as Reference Generator
    participant PolicyRepo as Policy Repository
    participant KafkaProducer as Kafka Producer
    participant DB as PostgreSQL

    Client->>GraphQL: createClaim(CreateClaimInput)
    GraphQL->>ClaimService: createClaim(CreateClaimCommand)

    ClaimService->>FnolRepo: findById(command.fnolId)
    FnolRepo->>DB: SELECT * FROM fnol WHERE id = ?
    DB-->>FnolRepo: FNOL entity
    FnolRepo-->>ClaimService: FNOL

    alt FNOL not found
        ClaimService-->>GraphQL: FnolNotFoundException
        GraphQL-->>Client: Error response
    else FNOL found
        ClaimService->>ClaimService: validateFnolEligibleForClaim(fnol)
        alt FNOL not eligible
            ClaimService-->>GraphQL: FnolNotEligibleException
            GraphQL-->>Client: Error response
        else FNOL eligible
            ClaimService->>PolicyRepo: findById(fnol.policyId)
            PolicyRepo->>DB: SELECT * FROM policy WHERE id = ?
            DB-->>PolicyRepo: Policy entity
            PolicyRepo-->>ClaimService: Policy

            ClaimService->>RefGen: next()
            RefGen->>DB: SELECT nextval('claim_no_seq')
            DB-->>RefGen: Next sequence number
            RefGen-->>ClaimService: Formatted claim number

            ClaimService->>ClaimService: assembleClaim(fnol, policy, command)
            ClaimService->>ClaimRepo: save(claim)
            ClaimRepo->>DB: INSERT INTO claim (...)
            DB-->>ClaimRepo: Claim with generated ID
            ClaimRepo-->>ClaimService: Saved claim

            ClaimService->>KafkaProducer: publishClaimCreated(claim)
            ClaimService-->>GraphQL: CreateClaimPayload(claim)
            GraphQL-->>Client: ClaimResponseDto
        end
    end
```

## 4. Document Upload Flow

```mermaid
sequenceDiagram
    participant Client
    participant CMX_UI as CMX-UI
    participant CMX_Uploader as CMX-Uploader
    participant CMX_BE as CMX-BE
    participant S3 as S3 Storage
    participant KafkaConsumer as Kafka Consumer
    participant FnolDocRepo as FNOL Document Repository
    participant DB as PostgreSQL

    Client->>CMX_UI: Select files for upload
    CMX_UI->>CMX_UI: Validate file types & sizes

    CMX_UI->>CMX_Uploader: POST /api/fnol/{fnolRef}/documents
    Note over CMX_UI,CMX_Uploader: Multipart file upload with metadata

    CMX_Uploader->>CMX_Uploader: Validate upload request
    CMX_Uploader->>CMX_Uploader: Generate unique file names
    CMX_Uploader->>S3: Upload files to bucket
    S3-->>CMX_Uploader: Upload confirmation

    CMX_Uploader->>CMX_Uploader: Extract metadata (EXIF, file info)
    CMX_Uploader->>CMX_Uploader: Generate thumbnails for images

    CMX_Uploader->>CMX_Uploader: Create DocumentUploadedEvent
    CMX_Uploader->>CMX_Uploader: Publish to Kafka topic 'document-events'

    Note over KafkaConsumer,DB: CMX-BE processes the event
    KafkaConsumer->>CMX_BE: DocumentUploaded event
    CMX_BE->>CMX_BE: Validate FNOL reference exists

    CMX_BE->>FnolDocRepo: findFnolByReference(fnolRef)
    FnolDocRepo->>DB: SELECT * FROM fnol WHERE fnol_reference_no = ?
    DB-->>FnolDocRepo: FNOL entity
    FnolDocRepo-->>CMX_BE: FNOL

    alt FNOL not found
        CMX_BE->>CMX_BE: Log error and send to dead letter queue
    else FNOL found
        CMX_BE->>FnolDocRepo: createFnolDocument(fnol, documentMetadata)
        FnolDocRepo->>DB: INSERT INTO fnol_document (...)
        DB-->>FnolDocRepo: FnolDocument entity
        FnolDocRepo-->>CMX_BE: Created document

        CMX_BE->>CMX_BE: Check if all required documents uploaded
        alt All required documents present
            CMX_BE->>CMX_BE: Update FNOL status if needed
            CMX_BE->>CMX_BE: Trigger next workflow step
        end

        CMX_BE->>CMX_UI: Send WebSocket notification
        CMX_UI->>Client: Update UI with upload confirmation
    end
```

## 5. Real-time Updates Flow (WebSocket)

```mermaid
sequenceDiagram
    participant Client
    participant CMX_UI as CMX-UI Frontend
    participant WebSocket as WebSocket Gateway
    participant CMX_BE as CMX-BE
    participant KafkaConsumer as Kafka Consumer
    participant SubscriptionManager as Subscription Manager
    participant Redis as Redis Cache

    Note over Client,Redis: Client establishes WebSocket connection
    Client->>CMX_UI: Open FNOL details page
    CMX_UI->>WebSocket: Connect to /ws/fnol-updates
    WebSocket->>SubscriptionManager: Register subscription
    SubscriptionManager->>Redis: Store subscription mapping

    Note over KafkaConsumer,Redis: Business event triggers update
    KafkaConsumer->>CMX_BE: SurveyorAssigned event
    CMX_BE->>SubscriptionManager: broadcastFnolUpdate(fnolId, update)

    SubscriptionManager->>Redis: Get subscribers for fnolId
    Redis-->>SubscriptionManager: List of connected clients

    SubscriptionManager->>WebSocket: Send update to subscribed clients
    WebSocket->>CMX_UI: FnolUpdateMessage
    CMX_UI->>CMX_UI: Update UI components
    CMX_UI->>Client: Display surveyor assignment notification

    Note over Client,Redis: Client disconnects
    Client->>CMX_UI: Navigate away or close browser
    CMX_UI->>WebSocket: Disconnect
    WebSocket->>SubscriptionManager: Unregister subscription
    SubscriptionManager->>Redis: Remove subscription mapping
```

## 6. Error Handling & Retry Flow

```mermaid
sequenceDiagram
    participant KafkaConsumer as Kafka Consumer
    participant EventProcessor as Event Processor
    participant BusinessService as Business Service
    participant DB as PostgreSQL
    participant ErrorHandler as Error Handler
    participant DeadLetterQueue as Dead Letter Queue
    participant AlertService as Alert Service
    participant RetryScheduler as Retry Scheduler

    KafkaConsumer->>EventProcessor: Process domain event
    EventProcessor->>BusinessService: Execute business logic

    alt Transient Error (e.g., DB connection timeout)
        BusinessService->>DB: Database operation
        DB-->>BusinessService: Connection timeout error
        BusinessService-->>EventProcessor: TransientException

        EventProcessor->>ErrorHandler: Handle transient error
        ErrorHandler->>ErrorHandler: Check retry count < max attempts
        alt Retry limit not reached
            ErrorHandler->>RetryScheduler: Schedule retry with backoff
            RetryScheduler->>RetryScheduler: Wait (exponential backoff)
            RetryScheduler->>EventProcessor: Retry event processing
        else Retry limit exceeded
            ErrorHandler->>DeadLetterQueue: Send to DLQ
            ErrorHandler->>AlertService: Send alert to operations team
        end

    else Permanent Error (e.g., business rule violation)
        BusinessService-->>EventProcessor: BusinessException
        EventProcessor->>ErrorHandler: Handle permanent error
        ErrorHandler->>DeadLetterQueue: Send to DLQ immediately
        ErrorHandler->>AlertService: Send alert with error details

    else Success
        BusinessService->>DB: Successful operation
        DB-->>BusinessService: Success response
        BusinessService-->>EventProcessor: Success
        EventProcessor->>EventProcessor: Commit Kafka offset
    end
```

## 7. Health Check & Circuit Breaker Flow

```mermaid
sequenceDiagram
    participant LoadBalancer as Load Balancer
    participant CMX_BE as CMX-BE
    participant Actuator as Spring Actuator
    participant HealthIndicator as Health Indicators
    participant DB as PostgreSQL
    participant Kafka as Kafka
    participant CircuitBreaker as Circuit Breaker
    participant ExternalAPI as External API

    Note over LoadBalancer,ExternalAPI: Health Check Flow
    LoadBalancer->>CMX_BE: GET /actuator/health
    CMX_BE->>Actuator: Check application health

    Actuator->>HealthIndicator: Check database health
    HealthIndicator->>DB: SELECT 1
    DB-->>HealthIndicator: Query result
    HealthIndicator-->>Actuator: DB Status: UP

    Actuator->>HealthIndicator: Check Kafka health
    HealthIndicator->>Kafka: Test connection
    Kafka-->>HealthIndicator: Connection result
    HealthIndicator-->>Actuator: Kafka Status: UP

    Actuator-->>CMX_BE: Overall Status: UP
    CMX_BE-->>LoadBalancer: 200 OK {"status": "UP"}

    Note over LoadBalancer,ExternalAPI: Circuit Breaker Flow
    CMX_BE->>CircuitBreaker: Call external service
    CircuitBreaker->>CircuitBreaker: Check circuit state

    alt Circuit CLOSED (normal operation)
        CircuitBreaker->>ExternalAPI: Make API call
        alt Call successful
            ExternalAPI-->>CircuitBreaker: Success response
            CircuitBreaker->>CircuitBreaker: Reset failure count
            CircuitBreaker-->>CMX_BE: Return response
        else Call failed
            ExternalAPI-->>CircuitBreaker: Error response
            CircuitBreaker->>CircuitBreaker: Increment failure count
            alt Failure threshold exceeded
                CircuitBreaker->>CircuitBreaker: OPEN circuit
                CircuitBreaker-->>CMX_BE: CircuitBreakerOpenException
            else Within threshold
                CircuitBreaker-->>CMX_BE: Propagate error
            end
        end

    else Circuit OPEN (failing fast)
        CircuitBreaker-->>CMX_BE: CircuitBreakerOpenException (immediate)

    else Circuit HALF_OPEN (testing recovery)
        CircuitBreaker->>ExternalAPI: Test call
        alt Test successful
            ExternalAPI-->>CircuitBreaker: Success response
            CircuitBreaker->>CircuitBreaker: CLOSE circuit
            CircuitBreaker-->>CMX_BE: Return response
        else Test failed
            ExternalAPI-->>CircuitBreaker: Error response
            CircuitBreaker->>CircuitBreaker: OPEN circuit again
            CircuitBreaker-->>CMX_BE: CircuitBreakerOpenException
        end
    end
```