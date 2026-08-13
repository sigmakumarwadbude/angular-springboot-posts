# 0.4 - REST API and Validation

## Goal

Harden the Posts REST API by introducing request validation and a consistent error response structure while preserving the existing architecture.

## Reading order

1. [Foundation](./04-rest-api-validation-foundation.md)
2. [API wiring](./04-rest-api-validation-api-wiring.md)
3. [Centralized error handling](./04-rest-api-validation-error-handling.md)
4. [Runtime behavior](./04-rest-api-validation-runtime-behavior.md)
5. [Delivery and verification](./04-rest-api-validation-delivery-and-verification.md)

## Architecture

```text
PostController
      ↓
PostService
      ↓
PostRepository
      ↓
JPA / Hibernate
      ↓
PostgreSQL
```

In this milestone, validation happens at the API boundary and invalid requests are mapped to `400 Bad Request` by a global exception handler.

## Milestone structure

```text
0.4 - REST API and Validation
├── Foundation
├── API wiring
├── Centralized error handling
├── Runtime behavior
└── Delivery and verification
```

## Files

- [Foundation](./04-rest-api-validation-foundation.md)
- [API wiring](./04-rest-api-validation-api-wiring.md)
- [Centralized error handling](./04-rest-api-validation-error-handling.md)
- [Runtime behavior](./04-rest-api-validation-runtime-behavior.md)
- [Delivery and verification](./04-rest-api-validation-delivery-and-verification.md)
