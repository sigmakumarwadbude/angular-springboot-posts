package com.example.postsapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Health",
        description = "Application health endpoints"
)
public class HealthController {

    @Operation(
            summary = "Check API health",
            description = "Returns the current application health status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Application is running"
    )
    @GetMapping("/api/health")
    public String health() {
        return "Posts API is running!";
    }
}