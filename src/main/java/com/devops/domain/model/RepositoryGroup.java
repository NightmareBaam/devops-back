package com.devops.domain.model;

import java.time.Instant;
import java.util.List;

public record RepositoryGroup(
        String id,
        String name,
        String description,
        List<OrderedRepository> repositories,
        Environment defaultEnvironment,
        String defaultTag,
        Instant updatedAt
) {
    public RepositoryGroup {
        repositories = repositories == null ? List.of() : List.copyOf(repositories);
    }
}
