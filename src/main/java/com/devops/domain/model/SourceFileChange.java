package com.devops.domain.model;

public record SourceFileChange(
        String path,
        SourceFileChangeType changeType
) {

    public SourceFileChange {
        path = DomainValidation.notBlank(path, "path");
        if (changeType == null) {
            throw new IllegalArgumentException("changeType must not be null");
        }
    }
}
