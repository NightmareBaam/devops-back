package com.devops.back.infra.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UtilisateurJiraRest(String displayName) {
}
