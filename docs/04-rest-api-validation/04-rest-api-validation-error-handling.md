# Centralized error handling

- [Back to chapter index](./04-rest-api-validation.md)
- [Previous: API wiring](./04-rest-api-validation-api-wiring.md)
- [Next: Runtime behavior](./04-rest-api-validation-runtime-behavior.md)

## 0.4.7 - Create `PostNotFoundException`

```java
package com.example.postsapi.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super("Post not found with id: " + id);
    }
}
```

## 0.4.8 - Add Global Exception Handling

Use `@RestControllerAdvice` to translate exceptions into consistent HTTP responses.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException exception,
                                                HttpServletRequest request) {
        // build validationErrors map and return ErrorResponse
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePostNotFound(PostNotFoundException exception,
                                            HttpServletRequest request) {
        // return 404 ErrorResponse
    }
}
```

## 0.4.9 - Understand Global Exception Handling

Controllers stay focused on request handling. Error-to-HTTP mapping is centralized, which makes the API easier to keep consistent as it grows.
