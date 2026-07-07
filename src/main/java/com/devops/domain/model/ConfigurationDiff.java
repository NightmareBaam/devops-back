package com.devops.domain.model;

public record ConfigurationDiff(
        String file,
        String key,
        String oldValue,
        String newValue,
        ConfigurationChangeType changeType
) {

    public ConfigurationDiff {
        file = DomainValidation.notBlank(file, "file");
        key = DomainValidation.notBlank(key, "key");
        oldValue = oldValue == null ? "" : oldValue;
        newValue = newValue == null ? "" : newValue;
        if (changeType == null) {
            throw new IllegalArgumentException("changeType must not be null");
        }
    }
}
