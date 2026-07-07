package com.devops.infra.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "devops.integrations")
public record IntegrationProperties(
        @Valid Server bitbucket,
        @Valid Server jira,
        @Valid Server confluence
) {

    public record Server(
            @NotBlank String baseUrl,
            String username,
            String password
    ) {
    }
}
