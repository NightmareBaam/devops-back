package com.devops.domain.model;

import java.util.Set;

public record JiraIssueInfo(
        String key,
        String title,
        String status,
        String assignee,
        Set<String> labels
) {

    public JiraIssueInfo {
        key = DomainValidation.notBlank(key, "key");
        title = title == null ? "" : title;
        status = status == null ? "" : status;
        assignee = assignee == null ? "" : assignee;
        labels = labels == null ? Set.of() : Set.copyOf(labels);
    }
}
