package com.devops.infra.jira;

import java.util.List;

record JiraFieldsResponse(
        String summary,
        JiraNamedValueResponse status,
        JiraUserResponse assignee,
        List<String> labels
) {
}
