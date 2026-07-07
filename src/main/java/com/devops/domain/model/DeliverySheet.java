package com.devops.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record DeliverySheet(
        String id,
        String title,
        String groupId,
        String groupName,
        Environment targetEnvironment,
        String status,
        String author,
        LocalDate deliveryDate,
        Instant createdAt,
        String confluenceUrl,
        String description,
        String jiraEpicKey,
        List<RepositoryRef> selectedApplications,
        List<String> jiraIssueKeys
) {
    public DeliverySheet {
        selectedApplications = selectedApplications == null ? List.of() : List.copyOf(selectedApplications);
        jiraIssueKeys = jiraIssueKeys == null ? List.of() : List.copyOf(jiraIssueKeys);
    }
}
