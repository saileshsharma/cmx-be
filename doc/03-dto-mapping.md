# DTO Mapping & Data Flow

## DTO Architecture Overview

```mermaid
graph TB
    subgraph "External Layer"
        GQL[GraphQL Schema]
        REST[REST APIs]
    end

    subgraph "Presentation Layer"
        INPUT[Input DTOs]
        OUTPUT[Output DTOs]
        PAYLOAD[Payload DTOs]
    end

    subgraph "Application Layer"
        CMD[Command Objects]
        QUERY[Query Objects]
        EVENT[Event DTOs]
    end

    subgraph "Domain Layer"
        ENTITY[JPA Entities]
        VO[Value Objects]
        ENUM[Enums]
    end

    subgraph "Infrastructure Layer"
        DB[(Database)]
        KAFKA[Kafka Topics]
        CACHE[(Cache)]
    end

    GQL --> INPUT
    INPUT --> CMD
    CMD --> ENTITY
    ENTITY --> DB

    DB --> ENTITY
    ENTITY --> OUTPUT
    OUTPUT --> GQL

    ENTITY --> EVENT
    EVENT --> KAFKA

    ENTITY --> CACHE
    CACHE --> OUTPUT
```

## DTO Mapping Strategy

### MapStruct Configuration
```java
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FnolMapper {

    // Entity to DTO
    FnolDto toDto(FNOL entity);
    List<FnolDto> toDtos(List<FNOL> entities);

    // Input DTO to Command
    CreateFnolCommand toCommand(CreateFnolInput input);

    // Entity to Response Payload
    FnolResponseDto toResponse(FNOL entity);

    // Custom mappings
    @Mapping(target = "accidentLocation", source = "accidentLocation")
    @Mapping(target = "policyDetails", source = "policy")
    FnolDetailsDto toDetailsDto(FNOL entity);
}
```

## FNOL Data Flow

```mermaid
sequenceDiagram
    participant Client
    participant Resolver as GraphQL Resolver
    participant Service as FNOL Service
    participant Mapper as MapStruct
    participant Entity as FNOL Entity
    participant Repo as Repository
    participant DB as Database

    Client->>Resolver: createFnol(CreateFnolInput)
    Resolver->>Mapper: toCommand(input)
    Mapper-->>Resolver: CreateFnolCommand
    Resolver->>Service: createFnol(command)

    Service->>Service: Validate business rules
    Service->>Entity: new FNOL()
    Service->>Entity: setProperties()
    Service->>Repo: save(fnol)
    Repo->>DB: INSERT
    DB-->>Repo: FNOL with ID
    Repo-->>Service: Saved FNOL

    Service->>Mapper: toPayload(fnol)
    Mapper-->>Service: CreateFnolPayload
    Service-->>Resolver: payload
    Resolver->>Mapper: toResponse(payload.fnol)
    Mapper-->>Resolver: FnolResponseDto
    Resolver-->>Client: FnolResponseDto
```

## DTO Class Structure

### Input DTOs (Mutations)

```mermaid
classDiagram
    class CreateFnolInput {
        +String policyNumber
        +String registrationNumber
        +Long accidentLocationId
        +String description
        +ClaimSeverity severity
        +String accidentDate
    }

    class UpdateFnolInput {
        +Long id
        +FNOLState fnolState
        +ClaimSeverity severity
        +String description
    }

    class CreateClaimInput {
        +Long fnolId
        +String claimType
        +Double estimatedAmount
        +String description
    }

    class CreateAddressInput {
        +String addressLine1
        +String addressLine2
        +String city
        +String province
        +String postalCode
        +String country
        +Double latitude
        +Double longitude
        +String googlePlaceId
        +LocationType locationType
    }
```

### Output DTOs (Responses)

```mermaid
classDiagram
    class FnolDto {
        +Long id
        +String fnolReferenceNo
        +String incidentDescription
        +String description
        +ClaimSeverity severity
        +LocalDateTime accidentDate
        +FNOLState fnolState
        +PolicyDto policy
        +VehicleDto vehicle
        +SurveyorDto surveyor
        +AddressDto accidentLocation
        +LocalDate updatedAt
    }

    class FnolDetailsDto {
        +Long id
        +String fnolReferenceNo
        +String description
        +ClaimSeverity severity
        +FNOLState state
        +PolicyDto policy
        +VehicleDto vehicle
        +AddressDto accidentLocation
        +List~FnolDocumentDto~ documents
        +List~ClaimDto~ claims
    }

    class ClaimDto {
        +Long id
        +String claimNumber
        +ClaimStatus status
        +Double claimAmount
        +LocalDate incidentDate
        +LocalDate claimDate
        +ClaimSeverity severity
        +String location
        +FnolDto fnol
        +PolicyDto policy
        +SurveyorDto surveyor
        +AddressDto address
    }

    class PolicyDto {
        +Long id
        +String policyNumber
        +String coverageType
        +PolicyStatus status
        +String lob
        +LocalDate startDate
        +LocalDate endDate
        +Double sumInsured
        +Double premium
        +InsuredDto insured
        +VehicleDto vehicle
    }
```

### Command Objects (Internal)

```mermaid
classDiagram
    class CreateFnolCommand {
        +String policyNumber
        +String registrationNumber
        +Long accidentLocationId
        +String description
        +ClaimSeverity severity
        +String accidentDate
    }

    class AssignSurveyorCommand {
        +Long fnolId
        +Long surveyorId
        +String assignmentReason
        +LocalDateTime assignmentDate
    }

    class UpdateClaimStatusCommand {
        +Long claimId
        +ClaimStatus newStatus
        +String reason
        +String updatedBy
    }
```

## Payload Wrappers

### Mutation Payloads

```mermaid
classDiagram
    class CreateFnolPayload {
        +FNOL fnol
        +Claim claim
        +String message
        +Boolean success
        +getFnol() FNOL
        +getClaim() Claim
    }

    class AssignmentPayload {
        +FNOL fnol
        +Surveyor surveyor
        +String assignmentId
        +LocalDateTime assignedAt
        +Boolean success
        +String message
    }

    class ClaimUpdatePayload {
        +Claim claim
        +String previousStatus
        +String newStatus
        +Boolean success
        +String message
    }
```

## Event DTOs

### Domain Events

```mermaid
classDiagram
    class FnolCreatedEvent {
        +String eventId
        +String eventType
        +String source
        +LocalDateTime timestamp
        +String subject
        +FnolEventData data
    }

    class FnolEventData {
        +String fnolReferenceNo
        +String policyNumber
        +String registrationNumber
        +ClaimSeverity severity
        +String accidentLocation
        +LocalDateTime accidentDate
    }

    class SurveyorAssignedEvent {
        +String eventId
        +String eventType
        +String source
        +LocalDateTime timestamp
        +String subject
        +AssignmentEventData data
    }

    class AssignmentEventData {
        +String fnolReferenceNo
        +Long surveyorId
        +String surveyorName
        +String assignmentType
        +LocalDateTime assignedAt
    }
```

## Validation & Constraints

### Input Validation

```java
public class CreateFnolInput {
    @NotBlank(message = "Policy number is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Invalid policy number format")
    private String policyNumber;

    @NotBlank(message = "Registration number is required")
    @Size(min = 3, max = 20, message = "Registration number must be 3-20 characters")
    private String registrationNumber;

    @NotNull(message = "Accident location is required")
    @Positive(message = "Invalid location ID")
    private Long accidentLocationId;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description too long")
    private String description;

    @NotNull(message = "Severity is required")
    private ClaimSeverity severity;

    @NotBlank(message = "Accident date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")
    private String accidentDate;
}
```

### Custom Validation

```java
@Component
public class FnolInputValidator {

    public void validateCreateFnolInput(CreateFnolInput input) {
        // Business rule validations
        validateAccidentDateNotFuture(input.getAccidentDate());
        validatePolicyNumberFormat(input.getPolicyNumber());
        validateRegistrationFormat(input.getRegistrationNumber());
    }

    private void validateAccidentDateNotFuture(String accidentDate) {
        LocalDateTime parsed = TimeParsers.parseAccidentDateOrThrow(accidentDate);
        if (parsed.isAfter(LocalDateTime.now())) {
            throw new ValidationException("Accident date cannot be in the future");
        }
    }
}
```

## Error Handling

### Error Response DTOs

```mermaid
classDiagram
    class ErrorResponse {
        +String code
        +String message
        +LocalDateTime timestamp
        +String path
        +List~FieldError~ fieldErrors
    }

    class FieldError {
        +String field
        +String message
        +Object rejectedValue
    }

    class BusinessError {
        +String errorCode
        +String errorMessage
        +Integer httpStatus
        +Map~String,Object~ context
    }
```

## Mapping Performance Considerations

### Lazy Loading Strategy
```java
@Mapper(componentModel = "spring")
public interface FnolMapper {

    // Shallow mapping for list views
    @Mapping(target = "policy.insured", ignore = true)
    @Mapping(target = "vehicle.engineDetails", ignore = true)
    FnolSummaryDto toSummaryDto(FNOL entity);

    // Deep mapping for detail views
    @Mapping(target = "policy", qualifiedByName = "mapPolicyWithDetails")
    @Mapping(target = "claims", qualifiedByName = "mapClaimsWithDetails")
    FnolDetailsDto toDetailsDto(FNOL entity);

    @Named("mapPolicyWithDetails")
    default PolicyDto mapPolicyWithDetails(Policy policy) {
        // Custom mapping logic for complex relationships
    }
}
```

### Caching Strategy
```java
@Service
@CacheConfig(cacheNames = "fnolDtos")
public class FnolMappingService {

    @Cacheable(key = "#fnol.id + '_summary'")
    public FnolSummaryDto toSummaryDto(FNOL fnol) {
        return fnolMapper.toSummaryDto(fnol);
    }

    @Cacheable(key = "#fnol.id + '_details'")
    public FnolDetailsDto toDetailsDto(FNOL fnol) {
        return fnolMapper.toDetailsDto(fnol);
    }
}
```

## API Documentation Integration

### GraphQL Schema Documentation
```graphql
"""
Input for creating a new FNOL (First Notice of Loss)
"""
input CreateFnolInput {
  """Policy number in format: POL-YYYYMMDD-XXXX"""
  policyNumber: String!

  """Vehicle registration number"""
  registrationNumber: String!

  """ID of the accident location address"""
  accidentLocationId: ID!

  """Description of the incident (max 1000 characters)"""
  description: String!

  """Severity level of the claim"""
  severity: ClaimSeverity!

  """Accident date and time in ISO format"""
  accidentDate: String!
}

"""
Response payload for FNOL creation
"""
type CreateFnolPayload {
  """The created FNOL record"""
  fnol: Fnol!

  """Associated claim if auto-generated"""
  claim: Claim

  """Success status"""
  success: Boolean!

  """Result message"""
  message: String
}
```