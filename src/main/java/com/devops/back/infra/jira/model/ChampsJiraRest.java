package com.devops.back.infra.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChampsJiraRest(
        String summary,
        StatutJiraRest status,
        UtilisateurJiraRest assignee,
        List<String> labels
) {

    public String statut() {
        return status == null ? null : status.name();
    }

    public String responsable() {
        return assignee == null ? null : assignee.displayName();
    }
}
