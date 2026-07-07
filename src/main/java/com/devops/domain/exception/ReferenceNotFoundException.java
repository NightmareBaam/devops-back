package com.devops.domain.exception;

import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.RepositoryCoordinates;

public class ReferenceNotFoundException extends RuntimeException {

    public ReferenceNotFoundException(RepositoryCoordinates repository, ComparisonReference reference) {
        super("Reference not found: " + reference.label() + " for " + repository.projectKey() + "/" + repository.slug());
    }
}
