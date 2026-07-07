package com.devops.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "devops.delivery-document")
public record DeliveryDocumentProperties(
        String spaceKey,
        String titlePrefix
) {

    public DeliveryDocumentProperties {
        spaceKey = hasText(spaceKey) ? spaceKey : "FLA";
        titlePrefix = hasText(titlePrefix) ? titlePrefix : "FLA";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
