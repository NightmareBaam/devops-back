package com.devops.domain.exception;

import com.devops.domain.model.RepositoryCoordinates;

public class RepositoryNotFoundException extends RuntimeException {

    public RepositoryNotFoundException(RepositoryCoordinates repository) {
        super("Repository not found: " + repository.projectKey() + "/" + repository.slug());
    }
}
