# Runtime behavior

- [Back to chapter index](./04-rest-api-validation.md)
- [Previous: Centralized error handling](./04-rest-api-validation-error-handling.md)
- [Next: Delivery and verification](./04-rest-api-validation-delivery-and-verification.md)

## 0.4.10 - Validation Error Flow

An invalid request never reaches `PostService`. Spring raises `MethodArgumentNotValidException`, and the global handler returns `400 Bad Request`.

## 0.4.11 - Resource Not Found Flow

Missing posts raise `PostNotFoundException`, which becomes a `404 Not Found` response.

## 0.4.12 - Valid Request

A valid request passes validation, reaches the service, and returns `201 Created`.

## 0.4.13 - 0.4.17 Tests

These sections cover the expected responses for:

- invalid title
- short title
- short body
- missing post
- deleting a missing post

The core idea is simple: invalid input fails fast, and missing resources are mapped consistently.
