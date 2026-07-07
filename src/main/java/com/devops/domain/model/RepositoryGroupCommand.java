package com.devops.domain.model;

import java.util.List;

public record RepositoryGroupCommand(
        String name,
        String description,
        List<RepositoryGroupRepositoryCommand> repositories,
        Environment defaultEnvironment,
        String defaultTag
) {
    public RepositoryGroupCommand {
        repositories = repositories == null ? List.of() : List.copyOf(repositories);
    }
}
