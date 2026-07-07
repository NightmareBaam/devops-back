package com.devops.domain.usecase;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.ComparisonReferenceType;
import com.devops.domain.model.DeploymentApplicationVersion;
import com.devops.domain.model.DeliveryAnalysisRequest;
import com.devops.domain.model.DeliveryApplicationSelection;
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
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzeApplicationsUseCaseTest {

    @Test
    void analyzesCommitsJiraIssuesAndConfigurationDiffsFromDeliveredEnvironmentToDevelop() {
        CapturingSourceControlPort sourceControlPort = new CapturingSourceControlPort();
        AnalyzeApplicationsUseCase useCase = new AnalyzeApplicationsUseCase(
                sourceControlPort,
                new StubDeploymentConfigurationPort(),
                new StubIssueTrackerPort()
        );
        ApplicationRepository repository = new ApplicationRepository("PRJ", "app", "Application", "url", "", List.of());

        List<ApplicationAnalysis> analyses = useCase.analyze(new DeliveryAnalysisRequest(List.of(
                new DeliveryApplicationSelection(repository, ComparisonReference.environment(Environment.REC))
        )));

        assertThat(sourceControlPort.fromReference.type()).isEqualTo(ComparisonReferenceType.TAG);
        assertThat(sourceControlPort.fromReference.value()).isEqualTo("1.0.0");
        assertThat(sourceControlPort.toReference.value()).isEqualTo("develop");
        assertThat(analyses).hasSize(1);

        ApplicationAnalysis analysis = analyses.getFirst();
        assertThat(analysis.commitCount()).isEqualTo(1);
        assertThat(analysis.commits().getFirst().jiraIssueKeys()).containsExactly("ABC-123");
        assertThat(analysis.commits().getFirst().configurationModified()).isTrue();
        assertThat(analysis.jiraIssues()).extracting(JiraIssueInfo::key).containsExactly("ABC-123");
        assertThat(analysis.configurationDiffs()).hasSize(1);
        assertThat(analysis.configurationDiffs().getFirst().key()).isEqualTo("feature.enabled");
        assertThat(analysis.errors()).isEmpty();
    }

    @Test
    void returnsPartialApplicationErrorWhenDeliveredVersionCannotBeResolved() {
        AnalyzeApplicationsUseCase useCase = new AnalyzeApplicationsUseCase(
                new CapturingSourceControlPort(),
                new EmptyDeploymentConfigurationPort(),
                new StubIssueTrackerPort()
        );
        ApplicationRepository repository = new ApplicationRepository("PRJ", "app", "Application", "url", "", List.of());

        List<ApplicationAnalysis> analyses = useCase.analyze(new DeliveryAnalysisRequest(List.of(
                new DeliveryApplicationSelection(repository, ComparisonReference.environment(Environment.REC))
        )));

        ApplicationAnalysis analysis = analyses.getFirst();
        assertThat(analysis.commits()).isEmpty();
        assertThat(analysis.errors()).containsExactly("No delivered version found for PRJ/app in REC");
    }

    private static class CapturingSourceControlPort implements SourceControlPort {
        private ComparisonReference fromReference;
        private ComparisonReference toReference;

        @Override
        public List<CommitInfo> listCommitsBetween(
                RepositoryCoordinates repository,
                ComparisonReference fromReference,
                ComparisonReference toReference
        ) {
            this.fromReference = fromReference;
            this.toReference = toReference;
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
            if (reference.type() == ComparisonReferenceType.TAG) {
                return Optional.of(new ApplicationConfigurationFile(
                        repository,
                        reference,
                        "application.yml",
                        "feature.enabled: false",
                        Map.of("feature.enabled", "false")
                ));
            }
            return Optional.of(new ApplicationConfigurationFile(
                    repository,
                    reference,
                    "application.yml",
                    "feature.enabled: true",
                    Map.of("feature.enabled", "true")
            ));
        }

        @Override
        public List<SourceFileChange> listChangedFiles(RepositoryCoordinates repository, String commitHash) {
            return List.of(new SourceFileChange("src/main/resources/application.yml", SourceFileChangeType.MODIFIED));
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
            return List.of(new RepositoryVersion(Environment.REC, "1.0.0", null));
        }

        @Override
        public Optional<RepositoryVersion> findDeliveredVersion(RepositoryCoordinates repository, Environment environment) {
            return Optional.of(new RepositoryVersion(environment, "1.0.0", null));
        }
    }

    private static class EmptyDeploymentConfigurationPort implements DeploymentConfigurationPort {
        @Override
        public Map<Environment, List<DeploymentApplicationVersion>> findDeliveredApplicationsByEnvironment() {
            return Map.of();
        }

        @Override
        public List<RepositoryVersion> findDeliveredVersions(RepositoryCoordinates repository) {
            return List.of();
        }

        @Override
        public Optional<RepositoryVersion> findDeliveredVersion(RepositoryCoordinates repository, Environment environment) {
            return Optional.empty();
        }
    }

    private static class StubIssueTrackerPort implements IssueTrackerPort {
        @Override
        public Map<String, JiraIssueInfo> findIssuesByKeys(Set<String> issueKeys) {
            return Map.of("ABC-123", new JiraIssueInfo("ABC-123", "Issue", "Open", "alice", Set.of()));
        }
    }
}
