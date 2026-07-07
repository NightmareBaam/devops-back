package com.devops.domain.model;

import java.util.Objects;

public record ComparisonReference(
        ComparisonReferenceType type,
        Environment environment,
        String value
) {

    public ComparisonReference {
        type = Objects.requireNonNull(type, "type must not be null");
        if (type == ComparisonReferenceType.ENVIRONMENT) {
            environment = Objects.requireNonNull(environment, "environment must not be null");
            value = environment.name();
        } else {
            value = DomainValidation.notBlank(value, "value");
            environment = null;
        }
    }

    public static ComparisonReference branch(String branchName) {
        return new ComparisonReference(ComparisonReferenceType.BRANCH, null, branchName);
    }

    public static ComparisonReference environment(Environment environment) {
        return new ComparisonReference(ComparisonReferenceType.ENVIRONMENT, environment, null);
    }

    public static ComparisonReference tag(String tagName) {
        return new ComparisonReference(ComparisonReferenceType.TAG, null, tagName);
    }

    public String label() {
        return value;
    }
}
