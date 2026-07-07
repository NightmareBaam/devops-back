package com.devops.domain.model;

import java.time.Instant;
import java.util.Objects;

public record RepositoryVersion(
        Environment environment,
        String version,
        Instant deliveredAt
) {

    public RepositoryVersion {
        environment = Objects.requireNonNull(environment, "environment must not be null");
        version = DomainValidation.notBlank(version, "version");
    }
}
