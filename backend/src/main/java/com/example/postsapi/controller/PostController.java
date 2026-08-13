package com.example.postsapi.controller;

import com.example.postsapi.domain.Post;
import com.example.postsapi.dto.PostRequest;
import com.example.postsapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Operations for managing posts.")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            summary = "Get all posts",
            description = "Returns all posts"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Posts retrieved successfully"
    )
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @Operation(
            summary = "Get a post",
            description = "Returns a post by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })    
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(
            @Parameter(description = "Identifier of the post to retrieve.", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @Operation(
            summary = "Create a post",
            description = "Creates a new post"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Post created successfully"
    )
    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest request) {
        Post createdPost = postService.createPost(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdPost);
    }

    @Operation(
            summary = "Update a post",
            description = "Updates an existing post"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @Parameter(description = "Identifier of the post to update.", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request) {

        Post updatedPost = postService.updatePost(id, request);

        return ResponseEntity.ok(updatedPost);
    }

    @Operation(
            summary = "Delete a post",
            description = "Deletes an existing post"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Post deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "Identifier of the post to delete.", example = "1")
            @PathVariable Long id) {

        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }
}