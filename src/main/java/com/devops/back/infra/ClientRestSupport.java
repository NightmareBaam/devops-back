package com.devops.back.infra;

import org.springframework.web.client.RestClient;

public final class ClientRestSupport {

    private ClientRestSupport() {
    }

    public static RestClient creer(String baseUrl, String username, String password) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    if (username != null && !username.isBlank()) {
                        headers.setBasicAuth(username, password == null ? "" : password);
                    }
                })
                .build();
    }
}
