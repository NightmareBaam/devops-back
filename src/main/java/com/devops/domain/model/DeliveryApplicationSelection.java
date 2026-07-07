package com.devops.domain.model;

import java.util.Objects;

public record DeliveryApplicationSelection(
        ApplicationRepository repository,
        ComparisonReference comparisonReference
) {

    public DeliveryApplicationSelection {
        repository = Objects.requireNonNull(repository, "repository must not be null");
        comparisonReference = Objects.requireNonNull(comparisonReference, "comparisonReference must not be null");
    }
}
