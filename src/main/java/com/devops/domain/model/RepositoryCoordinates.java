package com.devops.domain.model;

public record RepositoryCoordinates(
        String projectKey,
        String slug
) {

    public RepositoryCoordinates {
        projectKey = DomainValidation.notBlank(projectKey, "projectKey");
        slug = DomainValidation.notBlank(slug, "slug");
    }
}
