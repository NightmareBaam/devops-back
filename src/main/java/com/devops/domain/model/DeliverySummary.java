package com.devops.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record DeliverySummary(
        List<ApplicationAnalysis> applications,
        Set<String> jiraIssueKeys,
        int totalCommitCount,
        boolean hasConfigurationChanges,
        Instant preparedAt
) {

    public DeliverySummary {
        applications = applications == null ? List.of() : List.copyOf(applications);
        jiraIssueKeys = jiraIssueKeys == null ? Set.of() : Set.copyOf(jiraIssueKeys);
        if (totalCommitCount < 0) {
            throw new IllegalArgumentException("totalCommitCount must not be negative");
        }
        preparedAt = preparedAt == null ? Instant.now() : preparedAt;
    }
}
