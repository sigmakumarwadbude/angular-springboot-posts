# API wiring

- [Back to chapter index](./04-rest-api-validation.md)
- [Previous: Foundation](./04-rest-api-validation-foundation.md)
- [Next: Centralized error handling](./04-rest-api-validation-error-handling.md)

## 0.4.5 - Update `PostController`

The important change is to validate request bodies before the service is called:

```java
import jakarta.validation.Valid;

@PostMapping
public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest request) {
    Post createdPost = postService.createPost(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
}
```

Apply the same pattern to `PUT /api/posts/{id}`.

## 0.4.6 - Update `PostService`

`PostService` now consumes `PostRequest` for create and update operations:

```java
public Post createPost(PostRequest request) {
    Post post = new Post(request.getTitle(), request.getBody());
    return postRepository.save(post);
}
```

The service stays focused on persistence and business operations. Validation remains at the controller boundary.
