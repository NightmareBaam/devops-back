package com.devops.infra.jira;

import java.util.List;

record JiraSearchResponse(
        List<JiraIssueResponse> issues
) {
}
