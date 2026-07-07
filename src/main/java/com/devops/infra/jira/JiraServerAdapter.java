package com.devops.infra.jira;

import com.devops.domain.model.JiraIssueInfo;
import com.devops.domain.port.IssueLabelPort;
import com.devops.domain.port.IssueTrackerPort;
import com.devops.infra.config.BasicAuthRestClientFactory;
import com.devops.infra.config.IntegrationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JiraServerAdapter implements IssueTrackerPort, IssueLabelPort {

    private final RestClient restClient;

    public JiraServerAdapter(BasicAuthRestClientFactory restClientFactory, IntegrationProperties properties) {
        this.restClient = restClientFactory.create(properties.jira());
    }

    @Override
    public Map<String, JiraIssueInfo> findIssuesByKeys(Set<String> issueKeys) {
        if (issueKeys == null || issueKeys.isEmpty()) {
            return Map.of();
        }

        String jql = "key in (" + String.join(",", issueKeys) + ")";
        JiraSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/api/2/search")
                        .queryParam("jql", jql)
                        .queryParam("fields", "summary,status,assignee,labels")
                        .queryParam("maxResults", issueKeys.size())
                        .build())
                .retrieve()
                .body(JiraSearchResponse.class);

        List<JiraIssueResponse> issues = response == null || response.issues() == null ? List.of() : response.issues();
        return issues.stream()
                .map(this::toIssueInfo)
                .collect(Collectors.toUnmodifiableMap(JiraIssueInfo::key, issue -> issue));
    }

    @Override
    public void addLabels(Set<String> issueKeys, Set<String> labels) {
        if (issueKeys == null || issueKeys.isEmpty() || labels == null || labels.isEmpty()) {
            return;
        }

        for (String issueKey : issueKeys) {
            List<JiraUpdateOperation> operations = labels.stream()
                    .map(label -> new JiraUpdateOperation(label))
                    .toList();
            restClient.put()
                    .uri("/rest/api/2/issue/{issueKey}", issueKey)
                    .body(new JiraIssueUpdateRequest(Map.of("labels", operations)))
                    .retrieve()
                    .toBodilessEntity();
        }
    }

    private JiraIssueInfo toIssueInfo(JiraIssueResponse issue) {
        JiraFieldsResponse fields = issue.fields();
        return new JiraIssueInfo(
                issue.key(),
                fields == null ? "" : fields.summary(),
                fields == null || fields.status() == null ? "" : fields.status().name(),
                fields == null || fields.assignee() == null ? "" : fields.assignee().displayName(),
                fields == null || fields.labels() == null ? Set.of() : Set.copyOf(fields.labels())
        );
    }

    record JiraIssueUpdateRequest(
            Map<String, List<JiraUpdateOperation>> update
    ) {
    }

    record JiraUpdateOperation(
            String add
    ) {
    }
}
