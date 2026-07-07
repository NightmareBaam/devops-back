package com.devops.domain.model;

import java.time.Instant;
import java.util.Objects;

public record DeliveryDocumentRecord(
        String id,
        DeliverySummary summary,
        DeliveryDocument document,
        Instant createdAt
) {

    public DeliveryDocumentRecord {
        id = DomainValidation.notBlank(id, "id");
        summary = Objects.requireNonNull(summary, "summary must not be null");
        document = Objects.requireNonNull(document, "document must not be null");
        createdAt = createdAt == null ? Instant.now() : createdAt;
    }
}
