package com.devops.domain.model;

import java.time.Instant;
import java.util.Objects;

public record DeliveryAnalysisRecord(
        String id,
        ApplicationAnalysis analysis,
        Instant createdAt
) {

    public DeliveryAnalysisRecord {
        id = DomainValidation.notBlank(id, "id");
        analysis = Objects.requireNonNull(analysis, "analysis must not be null");
        createdAt = createdAt == null ? Instant.now() : createdAt;
    }
}
