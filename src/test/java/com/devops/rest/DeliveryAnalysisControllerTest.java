package com.devops.rest;

import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.DeploymentApplicationVersion;
import com.devops.domain.model.Environment;
import com.devops.domain.model.JiraIssueInfo;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.model.SourceFileChange;
import com.devops.domain.model.SourceFileChangeType;
import com.devops.domain.model.SourceReference;
import com.devops.domain.port.DeploymentConfigurationPort;
import com.devops.domain.port.IssueTrackerPort;
import com.devops.domain.port.SourceControlPort;
import com.devops.domain.usecase.AnalyzeApplicationsUseCase;
import com.devops.rest.controller.DeliveryAnalysisController;
import com.devops.rest.error.CorrelationIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeliveryAnalysisControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DeliveryAnalysisController controller = new DeliveryAnalysisController(new AnalyzeApplicationsUseCase(
                new StubSourceControlPort(),
                new StubDeploymentConfigurationPort(),
                new StubIssueTrackerPort()
        ));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .addFilters(new CorrelationIdFilter())
                .build();
    }

    @Test
    void analyzesApplications() throws Exception {
        String payload = """
                {
                  "applications": [
                    {
                      "repository": {
                        "projectKey": "PRJ",
                        "slug": "app",
                        "name": "Application",
                        "url": "https://bitbucket/app",
                        "description": "",
                        "versions": []
                      },
                      "comparisonReference": {
                        "type": "ENVIRONMENT",
                        "environment": "REC",
                        "value": "REC"
                      }
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/delivery-analyses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repository.slug").value("app"))
                .andExpect(jsonPath("$[0].commitCount").value(1))
                .andExpect(jsonPath("$[0].commits[0].jiraIssueKeys[0]").value("ABC-123"))
                .andExpect(jsonPath("$[0].jiraIssues[0].key").value("ABC-123"))
                .andExpect(jsonPath("$[0].configurationDiffs[0].key").value("feature.enabled"));
    }

    @Test
    void returnsBadRequestForInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/delivery-analyses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"applications\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    private static class StubSourceControlPort implements SourceControlPort {
        @Override
        public List<CommitInfo> listCommitsBetween(
                RepositoryCoordinates repository,
                ComparisonReference fromReference,
                ComparisonReference toReference
        ) {
            return List.of(new CommitInfo(
                    "abc",
                    "ABC-123 update configuration",
                    "alice",
                    Instant.parse("2026-07-02T00:00:00Z"),
                    Set.of(),
                    false
            ));
        }

        @Override
        public Optional<ApplicationConfigurationFile> readApplicationConfiguration(
                RepositoryCoordinates repository,
                ComparisonReference reference
        ) {
            boolean delivered = "1.2.3".equals(reference.value());
            return Optional.of(new ApplicationConfigurationFile(
                    repository,
                    reference,
                    "application.yml",
                    "",
                    Map.of("feature.enabled", delivered ? "false" : "true")
            ));
        }

        @Override
        public List<SourceFileChange> listChangedFiles(RepositoryCoordinates repository, String commitHash) {
            return List.of(new SourceFileChange("application.yml", SourceFileChangeType.MODIFIED));
        }

        @Override
        public List<SourceReference> listBranches(RepositoryCoordinates repository) {
            return List.of();
        }

        @Override
        public List<SourceReference> listTags(RepositoryCoordinates repository) {
            return List.of();
        }
    }

    private static class StubDeploymentConfigurationPort implements DeploymentConfigurationPort {
        @Override
        public Map<Environment, List<DeploymentApplicationVersion>> findDeliveredApplicationsByEnvironment() {
            return Map.of();
        }

        @Override
        public List<RepositoryVersion> findDeliveredVersions(RepositoryCoordinates repository) {
            return List.of(new RepositoryVersion(Environment.REC, "1.2.3", null));
        }

        @Override
        public Optional<RepositoryVersion> findDeliveredVersion(RepositoryCoordinates repository, Environment environment) {
            return Optional.of(new RepositoryVersion(environment, "1.2.3", null));
        }
    }

    private static class StubIssueTrackerPort implements IssueTrackerPort {
        @Override
        public Map<String, JiraIssueInfo> findIssuesByKeys(Set<String> issueKeys) {
            return Map.of("ABC-123", new JiraIssueInfo("ABC-123", "Issue", "Open", "alice", Set.of("delivery")));
        }
    }
}
