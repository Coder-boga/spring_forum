package com.example.forum.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("forum-system")
                .packagesToScan("com.example.forum.controller")
                .pathsToMatch("/**")
                .build();
    }
}
