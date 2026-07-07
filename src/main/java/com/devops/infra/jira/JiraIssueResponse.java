package com.devops.infra.jira;

record JiraIssueResponse(
        String key,
        JiraFieldsResponse fields
) {
}
