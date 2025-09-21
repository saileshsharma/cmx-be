# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CMX-BE is a Spring Boot 3.2.3 backend application for the Claims MotorX platform - an Auto Claims Management System. It exposes GraphQL APIs, persists domain data to PostgreSQL/PostGIS, emits domain events to Kafka, and integrates with CMX-Dispatcher (assignment engine) and CMX-Uploader (media service).

**Main Application Class**: `com.cb.th.claims.cmx.ClaimsApplication`

## Build & Development Commands

### Maven Commands
```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Build JAR
mvn clean package

# Run the application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker Commands
```bash
# Build Docker image
docker build -t cmx-be:latest .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f cmx-be
```

## Architecture Overview

### Core Technology Stack
- **Framework**: Spring Boot 3.2.3 with Java 17
- **API**: GraphQL (Spring GraphQL + GraphiQL)
- **Database**: PostgreSQL with PostGIS for spatial data
- **Messaging**: Apache Kafka
- **Security**: OAuth2 Resource Server with Keycloak
- **Caching**: Caffeine (local) and Redis (distributed)
- **Observability**: Micrometer, OpenTelemetry, Prometheus
- **Mapping**: MapStruct for DTO/Entity conversion
- **Utilities**: Lombok for boilerplate reduction

### Package Structure
```
com.cb.th.claims.cmx/
├── config/          # Configuration classes (security, GraphQL, Kafka, etc.)
├── entity/          # JPA entities (domain model)
├── dto/             # Data Transfer Objects
├── resolver/        # GraphQL resolvers (controllers)
├── service/         # Business logic services
├── repository/      # JPA repositories
├── controller/      # REST controllers and Kafka handlers
├── assign/          # Assignment engine integration
└── aop/             # Aspect-oriented programming (logging, etc.)
```

### Domain Model (Key Aggregates)
- **Policy**: Policy lifecycle management
- **Vehicle**: VIN/plate, make/model/year
- **Insured**: Party details
- **FNOL (First Notice of Loss)**: Incident capture with state machine
- **Claim**: Linked to FNOL, claim lifecycle
- **Surveyor**: Human resource with geo coverage
- **Address**: Spatial data with PostGIS POINT geometry

## Environment Configuration

### Development Environment
Use `.env.dev` for local development:
- Database: PostgreSQL on localhost:5432
- Kafka: localhost:9092
- Keycloak: localhost:9091
- Profile: `dev`

### Environment Files
- `.env.dev` - Development configuration
- `.env.demo` - Demo environment
- `.env.prod` - Production configuration

## GraphQL Schema

GraphQL schema files are located in `src/main/resources/graphql/`:
- `cmx-interface.graphqls` - Main interface definitions
- `fnol-notice.graphqls` - FNOL operations
- `cmx-enums.graphqls` - Enum definitions
- `claims-schema.graphqls` - Claims operations

Access GraphiQL at: `http://localhost:8080/graphiql` (dev profile)

## Key Business Rules

### FNOL (First Notice of Loss)
- Reference format: `TH-AT-FN-######`
- State machine: CREATED → VALIDATED → DISPATCHED → COMPLETED/REJECTED
- Immutable reference numbers

### Claims
- Claim number format: `TH-AT-CL-######`
- Linked to FNOL (1:1 or 1:N depending on jurisdiction)
- Status: OPEN, IN_REVIEW, APPROVED, REJECTED, CLOSED

### Security
- OAuth2 Resource Server with JWT validation
- Keycloak integration for authentication/authorization
- Profile-based security configuration

## Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=FnolServiceTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

## Development Notes

### Database
- Uses JPA/Hibernate with PostgreSQL
- PostGIS extension for spatial data
- Audit columns: created_at, updated_at, created_by, updated_by
- Database sequences for business ID generation

### Kafka Integration
- Producer/Consumer services in `controller/kafka/`
- CloudEvents-style event attributes
- Domain events: FnolCreated, FnolValidated, SurveyorAssigned, etc.

### Caching
- Local caching with Caffeine
- Distributed caching with Redis
- Enabled via `@EnableCaching`

### Observability
- Actuator endpoints at `/actuator/*`
- Prometheus metrics at `/actuator/prometheus`
- OpenTelemetry tracing support
- Health checks configured for Docker

### Code Quality
- MapStruct for DTO mapping
- Lombok for reducing boilerplate
- AOP for cross-cutting concerns (logging)
- Bean validation with `@Valid` annotations