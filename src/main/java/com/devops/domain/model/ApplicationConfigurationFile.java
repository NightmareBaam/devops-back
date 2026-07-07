package com.devops.domain.model;

import java.util.Map;
import java.util.Objects;

public record ApplicationConfigurationFile(
        RepositoryCoordinates repository,
        ComparisonReference reference,
        String path,
        String rawContent,
        Map<String, String> flattenedValues
) {

    public ApplicationConfigurationFile {
        repository = Objects.requireNonNull(repository, "repository must not be null");
        reference = Objects.requireNonNull(reference, "reference must not be null");
        path = DomainValidation.notBlank(path, "path");
        rawContent = rawContent == null ? "" : rawContent;
        flattenedValues = flattenedValues == null ? Map.of() : Map.copyOf(flattenedValues);
    }
}
