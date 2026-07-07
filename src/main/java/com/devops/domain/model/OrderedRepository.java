package com.devops.domain.model;

public record OrderedRepository(
        int order,
        ApplicationRepository repository
) {
}
