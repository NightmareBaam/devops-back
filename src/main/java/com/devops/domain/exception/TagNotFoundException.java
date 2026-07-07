package com.devops.domain.exception;

import com.devops.domain.model.RepositoryCoordinates;

public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(RepositoryCoordinates repository, String tagName) {
        super("Tag not found: " + tagName + " for " + repository.projectKey() + "/" + repository.slug());
    }
}
