package com.devops.domain.model;

import java.util.List;

public record DeliverySheetCreationResult(
        String id,
        String confluenceUrl,
        List<String> jiraIssueKeys
) {
    public DeliverySheetCreationResult {
        jiraIssueKeys = jiraIssueKeys == null ? List.of() : List.copyOf(jiraIssueKeys);
    }
}
