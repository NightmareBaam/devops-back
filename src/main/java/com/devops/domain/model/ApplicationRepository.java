package com.devops.domain.model;

import java.util.List;

public record ApplicationRepository(
        String projectKey,
        String slug,
        String name,
        String url,
        String description,
        List<RepositoryVersion> versions
) {

    public ApplicationRepository {
        projectKey = DomainValidation.notBlank(projectKey, "projectKey");
        slug = DomainValidation.notBlank(slug, "slug");
        name = DomainValidation.notBlank(name, "name");
        url = url == null ? "" : url;
        description = description == null ? "" : description;
        versions = versions == null ? List.of() : List.copyOf(versions);
    }

    public RepositoryCoordinates coordinates() {
        return new RepositoryCoordinates(projectKey, slug);
    }
}
