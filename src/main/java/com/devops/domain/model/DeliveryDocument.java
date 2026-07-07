package com.devops.domain.model;

import java.time.Instant;

public record DeliveryDocument(
        String id,
        String title,
        String spaceKey,
        String url,
        String content,
        DeliveryDocumentStatus status,
        Instant createdAt
) {

    public DeliveryDocument {
        id = id == null ? "" : id;
        title = DomainValidation.notBlank(title, "title");
        spaceKey = spaceKey == null ? "" : spaceKey;
        url = url == null ? "" : url;
        content = content == null ? "" : content;
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
    }
}
