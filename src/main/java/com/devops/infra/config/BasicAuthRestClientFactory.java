package com.devops.infra.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BasicAuthRestClientFactory {

    private final RestClient.Builder restClientBuilder;

    public BasicAuthRestClientFactory(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public RestClient create(IntegrationProperties.Server server) {
        RestClient.Builder builder = restClientBuilder.clone().baseUrl(server.baseUrl());
        if (hasText(server.username()) && hasText(server.password())) {
            builder.defaultHeaders(headers -> headers.setBasicAuth(server.username(), server.password()));
        }
        return builder.build();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
