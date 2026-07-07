package com.devops.domain.model;

import java.time.Instant;
import java.util.Objects;

public record DeploymentApplicationVersion(
        RepositoryCoordinates repository,
        Environment environment,
        String version,
        Instant deliveredAt
) {

    public DeploymentApplicationVersion {
        repository = Objects.requireNonNull(repository, "repository must not be null");
        environment = Objects.requireNonNull(environment, "environment must not be null");
        version = DomainValidation.notBlank(version, "version");
    }

    public RepositoryVersion toRepositoryVersion() {
        return new RepositoryVersion(environment, version, deliveredAt);
    }
}
