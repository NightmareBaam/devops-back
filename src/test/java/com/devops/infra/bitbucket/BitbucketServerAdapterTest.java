package com.devops.infra.bitbucket;

import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.SourceFileChange;
import com.devops.domain.model.SourceFileChangeType;
import com.devops.infra.config.BasicAuthRestClientFactory;
import com.devops.infra.config.IntegrationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BitbucketServerAdapterTest {

    private MockRestServiceServer server;
    private BitbucketServerAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new BitbucketServerAdapter(new BasicAuthRestClientFactory(builder), properties());
    }

    @Test
    void searchesRepositoriesAndMapsResponse() {
        server.expect(requestTo("http://bitbucket.local/rest/api/1.0/repos?name=app&limit=10"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, basicAuth()))
                .andRespond(withSuccess("""
                        {
                          "values": [
                            {
                              "slug": "app",
                              "name": "Application",
                              "description": "Backend app",
                              "url": "http://bitbucket.local/projects/PRJ/repos/app",
                              "project": { "key": "PRJ", "name": "Project" }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        List<ApplicationRepository> repositories = adapter.searchRepositories("app", 10);

        assertThat(repositories).hasSize(1);
        assertThat(repositories.getFirst().projectKey()).isEqualTo("PRJ");
        assertThat(repositories.getFirst().slug()).isEqualTo("app");
        server.verify();
    }

    @Test
    void listsCommitsAndChangedFiles() {
        RepositoryCoordinates repository = new RepositoryCoordinates("PRJ", "app");
        server.expect(requestTo("http://bitbucket.local/rest/api/1.0/projects/PRJ/repos/app/commits?since=1.0.0&until=develop&limit=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "values": [
                            {
                              "id": "abcdef",
                              "displayId": "abcdef",
                              "message": "ABC-123 change",
                              "authorName": "Alice",
                              "authorTimestamp": 1782950400000
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://bitbucket.local/rest/api/1.0/projects/PRJ/repos/app/commits/abcdef/changes?limit=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "values": [
                            {
                              "path": { "components": ["src", "main", "resources", "application.yml"] },
                              "type": "MODIFY"
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        List<CommitInfo> commits = adapter.listCommitsBetween(repository, ComparisonReference.tag("1.0.0"), ComparisonReference.branch("develop"));
        List<SourceFileChange> changes = adapter.listChangedFiles(repository, "abcdef");

        assertThat(commits).hasSize(1);
        assertThat(commits.getFirst().hash()).isEqualTo("abcdef");
        assertThat(changes).containsExactly(new SourceFileChange("src/main/resources/application.yml", SourceFileChangeType.MODIFIED));
        server.verify();
    }

    @Test
    void readsAndFlattensApplicationYaml() {
        RepositoryCoordinates repository = new RepositoryCoordinates("PRJ", "app");
        server.expect(requestTo("http://bitbucket.local/rest/api/1.0/projects/PRJ/repos/app/raw/application.yml?at=develop"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        server:
                          port: 8080
                        feature:
                          enabled: true
                        """, MediaType.TEXT_PLAIN));

        ApplicationConfigurationFile configuration = adapter
                .readApplicationConfiguration(repository, ComparisonReference.branch("develop"))
                .orElseThrow();

        assertThat(configuration.flattenedValues())
                .containsEntry("server.port", "8080")
                .containsEntry("feature.enabled", "true");
        server.verify();
    }

    private IntegrationProperties properties() {
        IntegrationProperties.Server bitbucket = new IntegrationProperties.Server("http://bitbucket.local", "user", "pass");
        IntegrationProperties.Server unused = new IntegrationProperties.Server("http://unused.local", "", "");
        return new IntegrationProperties(bitbucket, unused, unused);
    }

    private String basicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
    }
}
