package com.devops.domain.model;

final class DomainValidation {

    private DomainValidation() {
    }

    static String notBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
