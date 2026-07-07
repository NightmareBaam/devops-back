package com.devops.infra.jira;

import com.devops.domain.model.JiraIssueInfo;
import com.devops.infra.config.BasicAuthRestClientFactory;
import com.devops.infra.config.IntegrationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class JiraServerAdapterTest {

    private MockRestServiceServer server;
    private JiraServerAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new JiraServerAdapter(new BasicAuthRestClientFactory(builder), properties());
    }

    @Test
    void findsIssuesByKeysAndMapsFields() {
        server.expect(requestTo("http://jira.local/rest/api/2/search?jql=key%20in%20(ABC-123)&fields=summary,status,assignee,labels&maxResults=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "issues": [
                            {
                              "key": "ABC-123",
                              "fields": {
                                "summary": "Fix delivery",
                                "status": { "name": "In Progress" },
                                "assignee": { "displayName": "Alice" },
                                "labels": ["delivery", "backend"]
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        Map<String, JiraIssueInfo> issues = adapter.findIssuesByKeys(Set.of("ABC-123"));

        assertThat(issues).containsKey("ABC-123");
        assertThat(issues.get("ABC-123").title()).isEqualTo("Fix delivery");
        assertThat(issues.get("ABC-123").labels()).containsExactlyInAnyOrder("delivery", "backend");
        server.verify();
    }

    @Test
    void returnsEmptyMapWithoutCallingJiraWhenNoKeyIsProvided() {
        Map<String, JiraIssueInfo> issues = adapter.findIssuesByKeys(Set.of());

        assertThat(issues).isEmpty();
        server.verify();
    }

    @Test
    void addsLabelsOnIssues() {
        server.expect(requestTo("http://jira.local/rest/api/2/issue/ABC-123"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("""
                        {
                          "update": {
                            "labels": [
                              { "add": "livraison-ppr" }
                            ]
                          }
                        }
                        """))
                .andRespond(withNoContent());

        adapter.addLabels(Set.of("ABC-123"), Set.of("livraison-ppr"));

        server.verify();
    }

    private IntegrationProperties properties() {
        IntegrationProperties.Server unused = new IntegrationProperties.Server("http://unused.local", "", "");
        IntegrationProperties.Server jira = new IntegrationProperties.Server("http://jira.local", "user", "pass");
        return new IntegrationProperties(unused, jira, unused);
    }
}
