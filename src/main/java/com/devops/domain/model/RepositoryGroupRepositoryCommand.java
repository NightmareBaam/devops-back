package com.devops.domain.model;

public record RepositoryGroupRepositoryCommand(
        String projectKey,
        String slug,
        int order
) {
}
