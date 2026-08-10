package com.example.postsapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI postsApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Posts API")
                        .description("REST API for managing posts")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Posts API Team")));
    }
}