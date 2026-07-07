package com.devops.domain.model;

public record SourceReference(
        String name,
        SourceReferenceType type,
        String hash
) {

    public SourceReference {
        name = DomainValidation.notBlank(name, "name");
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        hash = hash == null ? "" : hash;
    }
}
