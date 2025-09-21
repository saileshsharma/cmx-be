# CMX-BE Documentation

This directory contains comprehensive documentation for the CMX Claims Management Backend system.

## Documentation Structure

### 📋 [01-architecture.md](./01-architecture.md)
**System Architecture & Design Patterns**
- System context and container diagrams
- Component architecture with package structure
- Technology stack overview
- Deployment architecture
- Design patterns (DDD, Event-driven, Clean Architecture)
- Configuration profiles and environment setup

### 🗄️ [02-database-schema.md](./02-database-schema.md)
**Database Design & Entity Relationships**
- Complete Entity Relationship Diagram (ERD)
- Database constraints and indexes
- Business identifier sequences
- Data types and enum definitions
- Audit strategies and temporal data
- Performance optimization indexes
- Future partitioning strategies

### 🔄 [03-dto-mapping.md](./03-dto-mapping.md)
**Data Transfer Objects & Mapping**
- DTO architecture and mapping strategy
- MapStruct configuration and usage
- Input/Output DTO structures
- Command objects and payload wrappers
- Event DTOs for domain events
- Validation strategies and error handling
- Performance considerations and caching

### 🚀 [04-fnol-workflow.md](./04-fnol-workflow.md)
**FNOL Business Process & State Machine**
- Complete FNOL state machine diagram
- End-to-end process flow
- Business rules and validation logic
- Surveyor assignment algorithms
- Integration points with external systems
- Error handling and recovery mechanisms
- Performance optimization and monitoring

### ⚡ [05-sequence-diagrams.md](./05-sequence-diagrams.md)
**Detailed Sequence Diagrams**
- FNOL creation flow with all validations
- Surveyor assignment process
- Claim creation workflow
- Document upload integration
- Real-time WebSocket updates
- Error handling and retry mechanisms
- Health checks and circuit breaker patterns

## Quick Navigation

### For Developers
- **Getting Started**: See [../CLAUDE.md](../CLAUDE.md) for development setup
- **API Design**: Review GraphQL schema in `src/main/resources/graphql/`
- **Database**: See [02-database-schema.md](./02-database-schema.md) for complete schema
- **Business Logic**: Review [04-fnol-workflow.md](./04-fnol-workflow.md) for process flows

### For Architects
- **System Design**: Start with [01-architecture.md](./01-architecture.md)
- **Data Flow**: Review [03-dto-mapping.md](./03-dto-mapping.md)
- **Integration**: See sequence diagrams in [05-sequence-diagrams.md](./05-sequence-diagrams.md)

### For Operations
- **Monitoring**: Health check patterns in [05-sequence-diagrams.md](./05-sequence-diagrams.md)
- **Error Handling**: Recovery mechanisms in [04-fnol-workflow.md](./04-fnol-workflow.md)
- **Performance**: Optimization strategies across all documents

## Key Business Concepts

### FNOL (First Notice of Loss)
The central business process where insurance claims begin. Flows through states:
```
DRAFT → SUBMITTED → ENRICHING → VALIDATED → DISPATCHED → COMPLETED
                                     ↓
                                REJECTED
```

### Domain Aggregates
- **Policy**: Insurance contract with coverage details
- **FNOL**: Incident report and processing workflow
- **Claim**: Financial claim against policy
- **Surveyor**: Human resource for claim investigation
- **Address**: Geographic locations with spatial data

### Integration Architecture
- **CMX-Dispatcher**: Handles surveyor assignment logic
- **CMX-Uploader**: Manages document and media upload
- **Kafka**: Event streaming for async processing
- **PostgreSQL/PostGIS**: Primary data store with spatial support

## Technology Stack Summary

### Core Framework
- Spring Boot 3.2.3 with Java 17
- GraphQL API with Spring GraphQL
- JPA/Hibernate with PostgreSQL
- Kafka for event streaming

### Key Libraries
- MapStruct for DTO mapping
- Lombok for boilerplate reduction
- Spring Security with OAuth2
- Micrometer for observability

### Infrastructure
- Docker containerization
- Redis for distributed caching
- Keycloak for identity management
- Prometheus/Grafana for monitoring

## Development Guidelines

### Code Organization
```
src/main/java/com/cb/th/claims/cmx/
├── config/          # Configuration classes
├── controller/      # REST and Kafka controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities (domain model)
├── enums/          # Business enumerations
├── exception/      # Custom exceptions
├── repository/     # Data access layer
├── resolver/       # GraphQL resolvers
├── service/        # Business logic
└── util/           # Utility classes
```

### Best Practices
- Follow Domain-Driven Design principles
- Use MapStruct for entity-DTO mapping
- Implement proper validation at input layer
- Handle errors gracefully with proper recovery
- Use events for async processing
- Maintain idempotency for critical operations

## Maintenance Notes

### Documentation Updates
This documentation should be updated when:
- New business processes are added
- API changes are made
- Database schema evolves
- Integration points change
- Performance characteristics change

### Version History
- **v1.0** - Initial CMX-BE implementation
- **Current** - Enhanced with comprehensive documentation

---

For questions or clarifications about this documentation, please refer to the development team or create an issue in the project repository.