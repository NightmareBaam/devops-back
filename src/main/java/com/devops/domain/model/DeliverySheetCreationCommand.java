package com.devops.domain.model;

import java.time.LocalDate;
import java.util.List;

public record DeliverySheetCreationCommand(
        String title,
        String groupId,
        Environment targetEnvironment,
        LocalDate deliveryDate,
        String responsible,
        String jiraEpicKey,
        String description,
        ConfluenceSelection confluence,
        DeliverySheetOptions options,
        List<RepositoryRef> selectedApplications,
        List<String> jiraIssueKeys
) {
    public DeliverySheetCreationCommand {
        selectedApplications = selectedApplications == null ? List.of() : List.copyOf(selectedApplications);
        jiraIssueKeys = jiraIssueKeys == null ? List.of() : List.copyOf(jiraIssueKeys);
    }
}
