# ADR-001 — Use PostgreSQL with JPA

- **Status:** Accepted
- **Date:** 2026-08-13
- **Decision Type:** Architecture
- **Milestone:** 0.3 — PostgreSQL / JPA Integration

## Context

The Posts API currently uses an H2 in-memory database for development.

The current persistence architecture is:

```text
PostController
      ↓
PostService
      ↓
PostRepository
      ↓
JPA / Hibernate
      ↓
H2
````

H2 is useful during the initial Spring Boot setup because it requires no external database installation or configuration.

However, the application should move toward a production-like database environment.

The Posts API requires a relational database that:

* Runs independently from the application
* Can be containerized for local development
* Supports standard SQL and relational data modeling
* Is suitable for production deployment
* Works well with Spring Data JPA and Hibernate

## Decision

We will replace H2 with **PostgreSQL** as the primary relational database for the Posts API.

Spring Data JPA and Hibernate will remain the persistence abstraction and ORM layer.

The resulting architecture will be:

```text
PostController
      ↓
PostService
      ↓
PostRepository
      ↓
Spring Data JPA
      ↓
Hibernate
      ↓
PostgreSQL
```

PostgreSQL will run as a Docker container during local development.

The application will connect to PostgreSQL through the PostgreSQL JDBC driver.

## Database Configuration

The development database will use:

| Property       | Value      |
| -------------- | ---------- |
| Database       | `postsdb`  |
| Username       | `postgres` |
| Password       | `postgres` |
| Port           | `5432`     |
| Docker service | `postgres` |

The JDBC connection will use:

```text
jdbc:postgresql://postgres:5432/postsdb
```

The Docker service name `postgres` is used as the hostname because the Spring Boot application and PostgreSQL database run on the same Docker network.

## Hibernate Configuration

Hibernate will use the PostgreSQL dialect:

```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

During development:

```properties
spring.jpa.hibernate.ddl-auto=update
```

will allow Hibernate to create and update the database schema based on the JPA domain model.

## Alternatives Considered

### H2

**Rejected as the primary development database.**

Advantages:

* Very easy to configure
* No external database required
* Fast startup
* Useful for initial learning

Disadvantages:

* In-memory data is lost when the application restarts
* SQL behavior can differ from PostgreSQL
* Does not provide a production-like database environment

H2 will no longer be used for the main development environment.

### MySQL

**Rejected.**

MySQL is a capable relational database, but PostgreSQL provides a better fit for this project's intended development and production environment.

### PostgreSQL

**Selected.**

Advantages:

* Production-grade relational database
* Excellent Spring Boot / JPA support
* Runs easily in Docker
* Strong SQL capabilities
* Suitable for local development and production
* Reduces differences between development and production environments

## Consequences

### Positive

* Development environment becomes closer to production.
* PostgreSQL-specific SQL behavior can be tested early.
* Database data can persist through Docker volumes.
* Existing JPA repositories and entities remain unchanged.
* The application retains database independence through JPA.

### Negative

* Local development requires PostgreSQL.
* Docker Compose configuration becomes more complex.
* Developers need to understand PostgreSQL connection configuration.
* Database startup and connectivity must be managed.

### Future Considerations

The current configuration uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This is appropriate for the learning/development stage.

Before production deployment, schema management should be migrated to a dedicated database migration tool such as:

* Flyway
* Liquibase

Production credentials should also be supplied through environment variables or a secure secrets-management mechanism rather than being committed to source control.

## Implementation

The PostgreSQL integration will include:

1. Remove the H2 runtime dependency.
2. Add the PostgreSQL JDBC driver.
3. Add PostgreSQL to Docker Compose.
4. Create the `postsdb` database.
5. Configure Spring Boot datasource properties.
6. Configure Hibernate for PostgreSQL.
7. Keep the existing JPA domain model.
8. Keep the existing `PostRepository`.
9. Verify Hibernate creates the `post` table.
10. Verify the existing Posts CRUD API against PostgreSQL.

## Verification

The decision is considered successfully implemented when:

* PostgreSQL starts successfully in Docker.
* Spring Boot connects to PostgreSQL.
* Hibernate initializes successfully.
* The `post` table is created.
* `GET /api/posts` works.
* `POST /api/posts` persists data.
* `GET /api/posts/{id}` retrieves persisted data.
* `PUT /api/posts/{id}` updates persisted data.
* `DELETE /api/posts/{id}` removes persisted data.
* Data can be verified directly in PostgreSQL.

## Status

**Accepted**

PostgreSQL is now the development database for the Posts API, with JPA/Hibernate retained as the persistence abstraction.

````

### Link it from the 0.3 documentation

At the beginning of `0.3.0-postgresql-jpa-integration.md`, add:

```md
## Architecture Decision

The decision to replace H2 with PostgreSQL is documented in:

- [ADR-001 — Use PostgreSQL with JPA](./adr/ADR-001-use-postgresql-with-jpa.md)
````

This gives the project a clean separation between **implementation documentation** and **architectural decisions**.
