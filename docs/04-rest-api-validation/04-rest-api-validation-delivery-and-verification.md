# Delivery and verification

- [Back to chapter index](./04-rest-api-validation.md)
- [Previous: Runtime behavior](./04-rest-api-validation-runtime-behavior.md)

## 0.4.18 - Document Validation in OpenAPI

The validation annotations on `PostRequest` are reflected in the generated schema, so Swagger UI shows the request constraints.

## 0.4.19 - 0.4.21 Build, Start, Test

These sections cover building the Docker image, starting the compose environment, and verifying the CRUD API end to end.

## 0.4.22 - Updated Project Structure

The project structure reflects the new `dto` and `exception` packages alongside controller, service, repository, and domain code.

## 0.4.23 - Updated Architecture

The final architecture is:

```text
Client -> PostController -> PostRequest DTO -> Validation
                                    |             |
                                    |             +-> GlobalExceptionHandler -> ErrorResponse
                                    |
                                    +-> PostService -> PostRepository -> PostgreSQL
```

## Verification checklist

- validation dependency added
- request DTO created
- `@Valid` added to POST and PUT
- validation errors return `400`
- missing posts return `404`
- OpenAPI still works
