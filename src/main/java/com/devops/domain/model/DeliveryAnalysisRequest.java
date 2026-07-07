package com.devops.domain.model;

import java.util.List;

public record DeliveryAnalysisRequest(
        List<DeliveryApplicationSelection> applications
) {

    public DeliveryAnalysisRequest {
        if (applications == null || applications.isEmpty()) {
            throw new IllegalArgumentException("applications must not be empty");
        }
        applications = List.copyOf(applications);
    }
}
