# 0.3 — PostgreSQL Database Integration

## Goal

Replace the **H2 in-memory database** with **PostgreSQL** while keeping the existing JPA-based repository/service/controller architecture.

In this step we will:

- Remove the H2 database dependency
- Add the PostgreSQL JDBC driver
- Configure PostgreSQL as the application database
- Create the `postsdb` PostgreSQL database
- Configure PostgreSQL connection properties
- Configure the PostgreSQL Hibernate dialect
- Enable automatic schema updates for development
- Enable formatted SQL logging
- Disable Open Session in View for REST API development
- Run PostgreSQL using Docker
- Connect the Spring Boot application to PostgreSQL
- Verify the database connection
- Verify Hibernate schema generation
- Verify the existing `/api/health` endpoint
- Verify the database using PostgreSQL tools
- Document common PostgreSQL connection problems

> **Note:** We will continue using `application.properties`, not YAML.

---

# 1. PostgreSQL Architecture

Previously, the application used an H2 in-memory database:

```text
Spring Boot
    │
    ▼
   JPA
    │
    ▼
 Hibernate
    │
    ▼
    H2
```

We are replacing H2 with PostgreSQL:

```text
Spring Boot
    │
    ▼
   JPA
    │
    ▼
 Hibernate
    │
    ▼
PostgreSQL
    │
    ▼
  postsdb
```

The application architecture remains:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
PostgreSQL
```

The database change should not require changes to the controller or service layers.

---

# 2. Prerequisites

Before starting this step, make sure you have:

- Java 21
- Maven / Maven Wrapper
- Docker
- Docker Compose
- PostgreSQL JDBC dependency
- The existing Posts API Spring Boot project

Verify Docker:

```bash
docker --version
```

Verify Docker Compose:

```bash
docker compose version
```

Verify Java:

```bash
java -version
```

Expected Java version:

```text
21
```

---

# 3. Remove H2 Dependency

The previous database configuration used H2.

The H2 dependency should no longer be required.

Open:

```text
backend/pom.xml
```

Find the H2 dependency:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

Remove it.

The application will now use PostgreSQL instead.

---

# 4. Add PostgreSQL JDBC Driver

Add the PostgreSQL JDBC driver to:

```text
backend/pom.xml
```

Add:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

The dependency allows Spring Boot/Hibernate to communicate with PostgreSQL through JDBC.

The relevant dependency section should contain:

```xml
<dependencies>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

</dependencies>
```

---

# 5. Refresh Maven Dependencies

From the `backend` directory:

### Windows

```bash
mvnw.cmd clean compile
```

### Linux / macOS

```bash
./mvnw clean compile
```

Maven should download the PostgreSQL JDBC driver.

A successful build should end with:

```text
BUILD SUCCESS
```

---

# 6. Create the PostgreSQL Database

The application will use a database named:

```text
postsdb
```

We will run PostgreSQL using Docker.

A typical development configuration is:

```text
Database: postsdb
Username: postgres
Password: postgres
Port: 5432
```

The PostgreSQL architecture is:

```text
PostgreSQL Container
        │
        ▼
     postgres
        │
        ▼
     postsdb
```

---

# 7. PostgreSQL Docker Configuration

Create or update the Docker Compose configuration used by the project.

Example:

```yaml
services:

  postgres:
    image: postgres:17
    container_name: posts-postgres
    environment:
      POSTGRES_DB: postsdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

This configuration creates:

| Setting | Value |
|---|---|
| PostgreSQL image | `postgres:17` |
| Container | `posts-postgres` |
| Database | `postsdb` |
| Username | `postgres` |
| Password | `postgres` |
| Host port | `5432` |
| Container port | `5432` |

> Use the PostgreSQL version already standardized by the project if a different version has been established in an earlier Docker step.

---

# 8. Start PostgreSQL

Start the PostgreSQL container:

```bash
docker compose up -d postgres
```

Verify the container:

```bash
docker ps
```

You should see a PostgreSQL container similar to:

```text
CONTAINER ID   IMAGE          PORTS
xxxxxxxx       postgres:17    0.0.0.0:5432->5432/tcp
```

---

# 9. Verify PostgreSQL Container

Check the PostgreSQL container logs:

```bash
docker logs posts-postgres
```

Look for a message indicating that PostgreSQL is ready to accept connections.

For example:

```text
database system is ready to accept connections
```

This confirms that PostgreSQL has successfully started.

---

# 10. Verify the PostgreSQL Database

Connect to PostgreSQL from the container:

```bash
docker exec -it posts-postgres psql -U postgres -d postsdb
```

You should see:

```text
psql
```

or a PostgreSQL prompt:

```text
postsdb=#
```

Run:

```sql
\conninfo
```

This displays information about the current PostgreSQL connection.

List databases:

```sql
\l
```

You should see:

```text
postsdb
```

List tables:

```sql
\dt
```

At this stage, there may not be any application tables yet because Hibernate has not necessarily created them.

Exit PostgreSQL:

```sql
\q
```

---

# 11. Open Application Configuration

The Spring Boot application configuration is located at:

```text
backend/
└── src/
    └── main/
        └── resources/
            └── application.properties
```

Open:

```text
src/main/resources/application.properties
```

---

# 12. Configure the Application

Replace the H2 configuration with the PostgreSQL configuration.

The application configuration should be:

```properties
# Application
spring.application.name=posts-api

# Server
server.port=8080

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/postsdb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres

# Hibernate / JPA
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Disable Open Session in View
spring.jpa.open-in-view=false
```

---

# 13. Application Name

```properties
spring.application.name=posts-api
```

Sets the Spring Boot application name.

The application is identified as:

```text
posts-api
```

---

# 14. Configure Server Port

```properties
server.port=8080
```

Spring Boot will listen on:

```text
http://localhost:8080
```

The application architecture is:

```text
Host
 │
 │ 8080
 ▼
Spring Boot
 │
 │ 5432
 ▼
PostgreSQL
```

---

# 15. Configure PostgreSQL JDBC URL

The PostgreSQL connection URL is:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postsdb
```

The URL contains:

```text
jdbc:postgresql://localhost:5432/postsdb
       │             │    │
       │             │    └── Database
       │             └────── PostgreSQL port
       └──────────────────── PostgreSQL JDBC driver
```

Therefore:

```text
Host     = localhost
Port     = 5432
Database = postsdb
```

When Spring Boot runs directly on the host machine and PostgreSQL runs in Docker with port `5432` published to the host, `localhost` is correct.

---

# 16. PostgreSQL JDBC Driver

Configure:

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
```

This tells Spring Boot which JDBC driver should be used to connect to PostgreSQL.

The driver comes from:

```xml
org.postgresql:postgresql
```

---

# 17. PostgreSQL Username

Configure:

```properties
spring.datasource.username=postgres
```

This must match the PostgreSQL user created by the Docker configuration:

```yaml
POSTGRES_USER: postgres
```

---

# 18. PostgreSQL Password

Configure:

```properties
spring.datasource.password=postgres
```

This must match:

```yaml
POSTGRES_PASSWORD: postgres
```

> For a real production application, credentials should not be committed directly to source control. Environment variables or a secrets-management solution should be used.

---

# 19. Configure PostgreSQL Hibernate Dialect

Configure:

```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

This tells Hibernate that the application uses PostgreSQL.

Hibernate uses the dialect to generate PostgreSQL-compatible SQL.

The flow is:

```text
JPA
 │
 ▼
Hibernate
 │
 ▼
PostgreSQLDialect
 │
 ▼
PostgreSQL SQL
```

---

# 20. Enable Automatic Schema Updates

Configure:

```properties
spring.jpa.hibernate.ddl-auto=update
```

During development, Hibernate automatically updates the database schema based on JPA entities.

For example:

```java
@Entity
public class Post {
    ...
}
```

Hibernate can create or update the corresponding PostgreSQL table.

The flow becomes:

```text
Post.java
    │
    ▼
@Entity
    │
    ▼
Hibernate
    │
    ▼
PostgreSQL
    │
    ▼
post table
```

> `update` is convenient for development. Production applications should generally use a controlled database migration strategy such as Flyway or Liquibase.

---

# 21. Enable SQL Logging

Configure:

```properties
spring.jpa.show-sql=true
```

Hibernate will display generated SQL in the application logs.

For example:

```text
Hibernate:
    select
        p1_0.id,
        p1_0.body,
        p1_0.title
    from
        post p1_0
```

This is useful when learning JPA and debugging database operations.

---

# 22. Enable Formatted SQL

Configure:

```properties
spring.jpa.properties.hibernate.format_sql=true
```

This formats generated SQL to make it easier to read.

Instead of:

```text
select p1_0.id,p1_0.body,p1_0.title from post p1_0
```

Hibernate logs formatted SQL:

```text
select
    p1_0.id,
    p1_0.body,
    p1_0.title
from
    post p1_0
```

---

# 23. Disable Open Session in View

Configure:

```properties
spring.jpa.open-in-view=false
```

This disables Spring's Open Session in View pattern.

For a REST API, this encourages database access to remain within the appropriate service/transaction boundary.

The desired architecture is:

```text
HTTP Request
     │
     ▼
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
JPA / Hibernate
     │
     ▼
PostgreSQL
```

Rather than allowing database access to leak into the HTTP response layer.

---

# 24. Complete `application.properties`

The final PostgreSQL configuration is:

```properties
# Application
spring.application.name=posts-api

# Server
server.port=8080

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/postsdb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres

# Hibernate / JPA
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Disable Open Session in View
spring.jpa.open-in-view=false
```

---

# 25. Important: Local Application vs Docker Application

The PostgreSQL hostname depends on where the Spring Boot application is running.

## Application running directly on the host

If Spring Boot is started with:

```bash
mvnw.cmd spring-boot:run
```

and PostgreSQL is running inside Docker with:

```text
5432:5432
```

use:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postsdb
```

Architecture:

```text
Windows / Host
│
├── Spring Boot
│     │
│     │ localhost:5432
│     ▼
│   Docker
│     │
│     ▼
│ PostgreSQL
```

---

## Application running inside Docker

If both Spring Boot and PostgreSQL run inside the same Docker Compose network, do **not** use `localhost`.

Use the PostgreSQL service name:

```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/postsdb
```

Architecture:

```text
Docker Network
│
├── posts-api
│       │
│       │ postgres:5432
│       ▼
└── postgres
        │
        ▼
      postsdb
```

This distinction is important.

Inside a container:

```text
localhost
```

means:

```text
the current container
```

It does not mean the PostgreSQL container.

---

# 26. Build the Application

From the `backend` directory:

### Windows

```bash
mvnw.cmd clean package
```

### Linux / macOS

```bash
./mvnw clean package
```

Expected:

```text
BUILD SUCCESS
```

---

# 27. Start PostgreSQL

Make sure PostgreSQL is running:

```bash
docker compose up -d postgres
```

Verify:

```bash
docker ps
```

---

# 28. Start the Spring Boot Application

For local development:

### Windows

```bash
mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

Spring Boot should start successfully.

Look for:

```text
Started PostsApplication
```

---

# 29. Verify PostgreSQL Connection

When the application starts, Hibernate should initialize successfully.

Look for PostgreSQL-related startup information in the logs.

You should not see errors such as:

```text
Unable to determine Dialect
```

or:

```text
Connection refused
```

or:

```text
password authentication failed
```

A successful startup confirms that:

```text
Spring Boot
     │
     ▼
DataSource
     │
     ▼
PostgreSQL JDBC Driver
     │
     ▼
PostgreSQL
     │
     ▼
postsdb
```

---

# 30. Verify Health Endpoint

The existing health endpoint should continue to work:

```http
GET http://localhost:8080/api/health
```

Expected response:

```text
Posts API is running
```

This confirms that the application is running.

> The health endpoint alone does not prove that every database operation works. Database connectivity should also be verified through the PostgreSQL logs and database inspection.

---

# 31. Verify Database from PostgreSQL

Connect to the database:

```bash
docker exec -it posts-postgres psql -U postgres -d postsdb
```

Run:

```sql
\conninfo
```

You should see that you are connected to:

```text
postsdb
```

List tables:

```sql
\dt
```

After JPA entities have been created, Hibernate should create the corresponding application tables.

For example:

```text
public | post | table | postgres
```

Exit:

```sql
\q
```

---

# 32. Verify Hibernate Schema Generation

If the `Post` entity already exists, restart the application and inspect the Hibernate logs.

Hibernate should generate SQL similar to:

```sql
create table post (
    id bigint generated by default as identity,
    body varchar(255),
    title varchar(255),
    primary key (id)
);
```

The exact generated SQL may vary depending on the Hibernate version and entity mapping.

The important result is that PostgreSQL contains the table.

Verify:

```sql
\dt
```

Then:

```sql
\d post
```

This displays the PostgreSQL table structure.

---

# 33. Verify Data Through PostgreSQL

If posts have been created through the REST API, query them directly:

```sql
SELECT * FROM post;
```

Example:

```text
 id | title              | body
----+--------------------+--------------------
  1 | First Post         | Hello PostgreSQL
```

This confirms the complete flow:

```text
HTTP Request
     │
     ▼
PostController
     │
     ▼
PostService
     │
     ▼
PostRepository
     │
     ▼
Hibernate
     │
     ▼
PostgreSQL
     │
     ▼
post
```

---

# 34. Test the CRUD API

Once the PostgreSQL database is connected, test the existing Posts API.

## Create Post

```http
POST http://localhost:8080/api/posts
Content-Type: application/json
```

Example body:

```json
{
  "title": "PostgreSQL Integration",
  "body": "This post is stored in PostgreSQL."
}
```

---

## Get All Posts

```http
GET http://localhost:8080/api/posts
```

Expected result should contain the created post.

---

## Get Post

```http
GET http://localhost:8080/api/posts/1
```

---

## Update Post

```http
PUT http://localhost:8080/api/posts/1
Content-Type: application/json
```

Example:

```json
{
  "title": "Updated PostgreSQL Post",
  "body": "The post was updated in PostgreSQL."
}
```

---

## Delete Post

```http
DELETE http://localhost:8080/api/posts/1
```

Then verify:

```sql
SELECT * FROM post;
```

The deleted record should no longer exist.

---

# 35. Verify Persistence

One of the main reasons for replacing H2 with PostgreSQL is persistent database storage.

With H2:

```text
Application starts
      │
      ▼
H2 database created
      │
      ▼
Application stops
      │
      ▼
Database disappears
```

With PostgreSQL and a Docker volume:

```text
Application
    │
    ▼
PostgreSQL
    │
    ▼
postgres-data volume
```

Restarting the Spring Boot application should not remove PostgreSQL data.

Verify by:

1. Creating a post.
2. Stopping Spring Boot.
3. Starting Spring Boot again.
4. Calling:

```http
GET /api/posts
```

The previously created data should still exist.

---

# 36. PostgreSQL Port 5432

PostgreSQL normally listens on:

```text
5432
```

The Docker mapping is:

```text
Host              Container
5432       ─────> 5432
```

Check whether port `5432` is already in use.

### Windows

```bash
netstat -ano | findstr :5432
```

If another PostgreSQL installation is already running, Docker may fail with an error similar to:

```text
Bind for 0.0.0.0:5432 failed:
port is already allocated
```

---

# 37. Troubleshooting — Port 5432 Already in Use

If port `5432` is occupied, identify the process:

```bash
netstat -ano | findstr :5432
```

You can also inspect Docker containers:

```bash
docker ps
```

Look for another PostgreSQL container.

If an existing PostgreSQL container is already providing the required database, use that database rather than starting another PostgreSQL instance.

Alternatively, map PostgreSQL to another host port.

For example:

```yaml
ports:
  - "5433:5432"
```

The application running on the host would then use:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/postsdb
```

Important:

```text
Host port     = 5433
Container port = 5432
```

The PostgreSQL service itself still listens on `5432` inside the container.

---

# 38. Troubleshooting — Connection Refused

Error:

```text
Connection refused
```

Check that PostgreSQL is running:

```bash
docker ps
```

Start it if necessary:

```bash
docker compose up -d postgres
```

Check logs:

```bash
docker logs posts-postgres
```

Make sure PostgreSQL is ready before starting the Spring Boot application.

---

# 39. Troubleshooting — Authentication Failed

Error:

```text
password authentication failed for user "postgres"
```

Check:

```yaml
POSTGRES_USER: postgres
POSTGRES_PASSWORD: postgres
```

and compare them with:

```properties
spring.datasource.username=postgres
spring.datasource.password=postgres
```

They must match.

Also verify that the database is the expected PostgreSQL instance.

---

# 40. Troubleshooting — Database Does Not Exist

Error:

```text
database "postsdb" does not exist
```

Check PostgreSQL databases:

```bash
docker exec -it posts-postgres psql -U postgres -c "\l"
```

If necessary, create the database:

```bash
docker exec -it posts-postgres psql -U postgres -c "CREATE DATABASE postsdb;"
```

---

# 41. Troubleshooting — Wrong Hostname

If Spring Boot runs locally:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postsdb
```

If Spring Boot runs inside Docker Compose:

```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/postsdb
```

Do not use:

```properties
jdbc:postgresql://localhost:5432/postsdb
```

from inside the application container when PostgreSQL is running in another container.

---

# 42. Troubleshooting — Hibernate Dialect

For PostgreSQL:

```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Do not leave the H2 dialect:

```properties
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

The H2 dialect must be removed when PostgreSQL becomes the application database.

---

# 43. Troubleshooting — H2 References

Search the project for:

```text
h2
```

and verify that H2-specific configuration has been removed.

Search for:

```text
H2Dialect
```

There should be no H2 dialect configuration in the PostgreSQL setup.

Also remove H2-specific properties such as:

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

The H2 console is no longer applicable.

---

# 44. H2 vs PostgreSQL

| Feature | H2 | PostgreSQL |
|---|---|---|
| Database type | In-memory | Server database |
| Persistence | Temporary | Persistent |
| JDBC URL | `jdbc:h2:mem:postsdb` | `jdbc:postgresql://localhost:5432/postsdb` |
| Driver | `org.h2.Driver` | `org.postgresql.Driver` |
| Username | `sa` | `postgres` |
| Password | Empty | Configured password |
| Hibernate dialect | `H2Dialect` | `PostgreSQLDialect` |
| H2 console | Available | Not applicable |
| Docker database | Not required | PostgreSQL container |
| Production-like database | No | Yes |
| Data survives app restart | No | Yes |

---

# 45. Updated Project Architecture

After PostgreSQL integration:

```text
                    Docker
                       │
          ┌────────────┴────────────┐
          │                         │
          ▼                         ▼
   ┌───────────────┐        ┌───────────────┐
   │ Spring Boot   │        │  PostgreSQL   │
   │ Posts API     │───────▶│               │
   │               │  JDBC  │   postsdb     │
   │ Java 21       │        │               │
   └───────┬───────┘        └───────┬───────┘
           │                         │
           ▼                         ▼
      REST API                  PostgreSQL
      /api/posts                   tables
```

Application architecture:

```text
                    REST API
                       │
                       ▼
                PostController
                       │
                       ▼
                  PostService
                       │
                       ▼
                PostRepository
                       │
                       ▼
                   JPA
                       │
                       ▼
                  Hibernate
                       │
                       ▼
                 PostgreSQL
```

---

# 46. Configuration Summary

The important configuration changes are:

### Remove H2

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

### Add PostgreSQL

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Replace H2 JDBC URL

From:

```properties
spring.datasource.url=jdbc:h2:mem:postsdb
```

To:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postsdb
```

### Replace driver

From:

```properties
spring.datasource.driver-class-name=org.h2.Driver
```

To:

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Replace username

From:

```properties
spring.datasource.username=sa
```

To:

```properties
spring.datasource.username=postgres
```

### Replace password

From:

```properties
spring.datasource.password=
```

To:

```properties
spring.datasource.password=postgres
```

### Replace Hibernate dialect

From:

```properties
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

To:

```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Remove H2 console

Remove:

```properties
spring.h2.console.enabled=true
spring.h2.console.settings.web-allow-others=true
spring.h2.console.path=/h2-console
```

---

# 47. Verification Checklist

- [ ] H2 dependency removed from `pom.xml`
- [ ] PostgreSQL JDBC dependency added
- [ ] Maven dependencies successfully downloaded
- [ ] PostgreSQL Docker container created
- [ ] PostgreSQL container is running
- [ ] PostgreSQL is listening on port `5432`
- [ ] `postsdb` database exists
- [ ] PostgreSQL username configured
- [ ] PostgreSQL password configured
- [ ] PostgreSQL JDBC URL configured
- [ ] PostgreSQL JDBC driver configured
- [ ] PostgreSQL Hibernate dialect configured
- [ ] `ddl-auto=update` configured
- [ ] SQL logging enabled
- [ ] SQL formatting enabled
- [ ] Open Session in View disabled
- [ ] H2 configuration removed
- [ ] Application starts successfully
- [ ] `/api/health` works
- [ ] Spring Boot connects to PostgreSQL
- [ ] Hibernate initializes successfully
- [ ] JPA creates/updates the application schema
- [ ] PostgreSQL tables can be inspected using `psql`
- [ ] Posts CRUD operations work against PostgreSQL
- [ ] Data survives Spring Boot restart

---

# 48. Final Result

The Posts API no longer depends on the H2 in-memory database.

The final development environment is:

```text
┌──────────────────────────────────────────┐
│              Posts API                   │
│                                          │
│ Java 21                                  │
│ Spring Boot                              │
│ Spring Data JPA                          │
│ Hibernate                                │
│ REST Controllers                         │
└───────────────────┬──────────────────────┘
                    │
                    │ JDBC
                    ▼
┌──────────────────────────────────────────┐
│             PostgreSQL                   │
│                                          │
│ Database: postsdb                        │
│ Port: 5432                               │
│ User: postgres                            │
└───────────────────┬──────────────────────┘
                    │
                    ▼
             postgres-data
                 volume
```

The application now uses a real PostgreSQL database while retaining the existing:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
```

architecture.

This provides a more production-like persistence layer and prepares the Posts API for the next stages of development.

---

# Next Step

**0.4 — Create Post Entity**

We will create the JPA `Post` entity and allow Hibernate to create the corresponding `post` table in the PostgreSQL `postsdb` database.