package com.example.postsapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {

    @NotBlank(message = "Title is required")
    @Size(
            min = 3,
            max = 100,
            message = "Title must be between 3 and 100 characters"
    )
    private String title;

    @NotBlank(message = "Body is required")
    @Size(
            min = 10,
            max = 5000,
            message = "Body must be between 10 and 5000 characters"
    )
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