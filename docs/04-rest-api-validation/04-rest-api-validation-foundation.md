# Foundation

- [Back to chapter index](./04-rest-api-validation.md)
- [Next: API wiring](./04-rest-api-validation-api-wiring.md)

## 0.4.1 - Add Bean Validation

Add the validation starter to `backend/pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## 0.4.2 - Define Post Validation Rules

Use these rules for the initial API:

- `title`: required, 3-100 characters
- `body`: required, 10-5000 characters

## 0.4.3 - Create `PostRequest`

```java
package com.example.postsapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Body is required")
    @Size(min = 10, max = 5000, message = "Body must be between 10 and 5000 characters")
    private String body;

    public PostRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
```

## 0.4.4 - Create `ErrorResponse`

Use a shared error envelope instead of returning ad hoc maps:

```java
package com.example.postsapi.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;

    // getters, setters, constructors
}
```

This keeps validation errors and resource errors under the same top-level format.
