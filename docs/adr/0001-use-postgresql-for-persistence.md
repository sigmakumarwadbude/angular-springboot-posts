# ADR-0001: Use PostgreSQL for Persistence

- **Status:** Accepted
- **Date:** 2026-08-12
- **Decision:** Replace the in-memory H2 database with PostgreSQL
- **Scope:** Spring Boot Posts API

## Context

The Posts API initially uses H2 as an in-memory database for development and early API implementation.

H2 is useful for quickly validating the JPA-based persistence layer, but it does not represent the production database environment we intend to use.

The application already follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
JPA Repository
    ↓
Database
```

The goal is to introduce PostgreSQL without changing the existing application architecture or replacing Spring Data JPA.

## Decision

We will use **PostgreSQL** as the primary relational database for the Posts API.

Spring Data JPA and Hibernate will remain responsible for persistence abstraction and ORM functionality.

The application will continue to use:

- JPA entities for domain persistence
- Spring Data JPA repositories
- Service-layer business logic
- REST controllers
- Hibernate as the JPA implementation
- PostgreSQL as the relational database

The persistence flow will therefore become:

```text
PostController
      ↓
PostService
      ↓
PostRepository
      ↓
Hibernate / JPA
      ↓
PostgreSQL
```

## Database Configuration

PostgreSQL will be configured through Spring Boot datasource properties rather than hard-coded database connection details.

The application will use environment variables for values such as:

- Database host
- Database port
- Database name
- Database username
- Database password

This allows the same application configuration to work across different environments.

## Development Environment

PostgreSQL will run as a Docker container during local development.

The application will connect to PostgreSQL through the Docker Compose service name rather than relying on a machine-specific hostname.

Example architecture:

```text
┌─────────────────────┐
│   Spring Boot API   │
│      :8080          │
└──────────┬──────────┘
           │
           │ JDBC
           ▼
┌─────────────────────┐
│     PostgreSQL      │
│       :5432         │
└─────────────────────┘
```

## JPA Schema Management

Hibernate will manage the database schema during development.

The development configuration may use:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

This allows the existing `Post` entity to be mapped to a PostgreSQL table without introducing database migrations at this stage.

Database migrations are intentionally deferred until a later milestone.

## Consequences

### Positive

- Uses a production-grade relational database.
- Provides a development environment closer to production.
- Keeps the existing JPA repository/service/controller architecture unchanged.
- Allows developers to use PostgreSQL locally through Docker.
- Makes database configuration environment-specific.
- Provides a natural foundation for future database migrations.

### Negative

- Local development now requires PostgreSQL.
- Docker is required for the recommended development setup.
- Database state persists between application restarts when using a PostgreSQL volume.
- PostgreSQL configuration is more involved than the original H2 setup.

## Alternatives Considered

### Continue Using H2

Rejected.

H2 is convenient for tests and quick prototypes, but continuing to use it as the primary development database would create a larger difference between development and the intended production environment.

### Replace JPA with JDBC

Rejected.

The existing application already uses Spring Data JPA successfully. Replacing it would introduce unnecessary architectural changes and additional implementation work.

### Use PostgreSQL Without Docker

Rejected for the standard development workflow.

Developers could install PostgreSQL directly on their machines, but Docker provides a more consistent and reproducible development environment.

### Introduce Flyway/Liquibase Immediately

Deferred.

Database migrations are important for production-grade schema management, but introducing a migration framework is outside the scope of this milestone.

A future ADR can document the migration strategy.

## Impact on Existing Architecture

No changes are required to the application layers.

The following components remain unchanged:

```text
PostController
PostService
PostRepository
Post
```

Only the persistence infrastructure changes:

```text
Before:

PostRepository
      ↓
Hibernate
      ↓
H2


After:

PostRepository
      ↓
Hibernate
      ↓
PostgreSQL
```

This demonstrates the benefit of using JPA as a persistence abstraction.

## Verification

The decision is considered successfully implemented when:

- [ ] H2 dependency is removed.
- [ ] PostgreSQL JDBC dependency is added.
- [ ] PostgreSQL starts successfully through Docker.
- [ ] Spring Boot connects successfully to PostgreSQL.
- [ ] Hibernate creates the `posts` table.
- [ ] Existing CRUD APIs continue to work.
- [ ] Data survives application restarts.
- [ ] PostgreSQL connection details are provided through configuration/environment variables.
- [ ] Existing controller, service, repository, and entity architecture remains intact.

## Related Decisions

Future ADRs may document:

- Database migration strategy
- PostgreSQL production deployment
- Database indexing strategy
- Transaction management
- Test database strategy
- Database backup and recovery
- Connection pooling