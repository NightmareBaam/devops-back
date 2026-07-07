package com.devops.domain.model;

public record RepositoryRef(
        String projectKey,
        String slug
) {
}
