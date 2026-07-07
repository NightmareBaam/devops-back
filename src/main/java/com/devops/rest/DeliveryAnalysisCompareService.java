package com.devops.rest;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.ComparisonReferenceType;
import com.devops.domain.model.ConfigurationDiff;
import com.devops.domain.model.JiraIssueInfo;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.SourceFileChange;
import com.devops.domain.port.DeploymentConfigurationPort;
import com.devops.domain.port.IssueTrackerPort;
import com.devops.domain.port.RepositoryCatalogPort;
import com.devops.domain.port.SourceControlPort;
import com.devops.domain.service.ConfigurationChangeDetector;
import com.devops.domain.service.ConfigurationDiffService;
import com.devops.domain.service.JiraIssueKeyExtractor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeliveryAnalysisCompareService {

    private final RepositoryCatalogPort repositoryCatalogPort;
    private final SourceControlPort sourceControlPort;
    private final DeploymentConfigurationPort deploymentConfigurationPort;
    private final IssueTrackerPort issueTrackerPort;
    private final JiraIssueKeyExtractor jiraIssueKeyExtractor = new JiraIssueKeyExtractor();
    private final ConfigurationChangeDetector configurationChangeDetector = new ConfigurationChangeDetector();
    private final ConfigurationDiffService configurationDiffService = new ConfigurationDiffService();

    public DeliveryAnalysisCompareService(
            RepositoryCatalogPort repositoryCatalogPort,
            SourceControlPort sourceControlPort,
            DeploymentConfigurationPort deploymentConfigurationPort,
            IssueTrackerPort issueTrackerPort
    ) {
        this.repositoryCatalogPort = repositoryCatalogPort;
        this.sourceControlPort = sourceControlPort;
        this.deploymentConfigurationPort = deploymentConfigurationPort;
        this.issueTrackerPort = issueTrackerPort;
    }

    public List<ComparisonAnalysisResponse> compare(ComparisonAnalysisRequest request) {
        return request.applications().stream()
                .map(this::compareApplication)
                .toList();
    }

    private ComparisonAnalysisResponse compareApplication(ComparisonApplicationRequest request) {
        List<String> errors = new ArrayList<>();
        RepositoryCoordinates coordinates = request.repository();
        ApplicationRepository repository = repositoryCatalogPort.findRepository(coordinates)
                .orElse(new ApplicationRepository(coordinates.projectKey(), coordinates.slug(), coordinates.slug(), "", "", List.of()));

        try {
            ComparisonReference baseReference = resolveReference(coordinates, request.baseReference());
            ComparisonReference targetReference = resolveReference(coordinates, request.targetReference());
            List<CommitInfo> commits = sourceControlPort
                    .listCommitsBetween(coordinates, baseReference, targetReference)
                    .stream()
                    .map(commit -> enrichCommit(coordinates, commit, errors))
                    .toList();
            List<ConfigurationDiff> diffs = configurationDiffs(coordinates, baseReference, targetReference, errors);
            List<JiraIssueInfo> issues = jiraIssues(commits, errors);
            int modifiedFileCount = modifiedFileCount(coordinates, commits, errors);

            ApplicationAnalysis analysis = new ApplicationAnalysis(repository, request.baseReference(), commits, issues, diffs, errors);
            return ComparisonAnalysisResponse.from(analysis, request.baseReference(), request.targetReference(), modifiedFileCount);
        } catch (RuntimeException exception) {
            errors.add(exception.getMessage());
            return new ComparisonAnalysisResponse(
                    repository,
                    request.baseReference(),
                    request.targetReference(),
                    0,
                    0,
                    false,
                    Set.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    errors
            );
        }
    }

    private ComparisonReference resolveReference(RepositoryCoordinates repository, ComparisonReference reference) {
        if (reference.type() != ComparisonReferenceType.ENVIRONMENT) {
            return reference;
        }
        return deploymentConfigurationPort.findDeliveredVersion(repository, reference.environment())
                .map(version -> ComparisonReference.tag(version.version()))
                .orElseThrow(() -> new IllegalStateException("No delivered version found for " + repository.projectKey() + "/" + repository.slug() + " in " + reference.environment()));
    }

    private CommitInfo enrichCommit(RepositoryCoordinates repository, CommitInfo commit, List<String> errors) {
        Set<String> issueKeys = jiraIssueKeyExtractor.extract(commit.message());
        boolean configurationModified = commit.configurationModified();
        try {
            configurationModified = configurationModified
                    || configurationChangeDetector.hasApplicationConfigurationChange(sourceControlPort.listChangedFiles(repository, commit.hash()));
        } catch (RuntimeException exception) {
            errors.add("Unable to read changed files for commit " + commit.hash() + ": " + exception.getMessage());
        }
        return commit.withJiraIssueKeys(issueKeys).withConfigurationModified(configurationModified);
    }

    private List<ConfigurationDiff> configurationDiffs(
            RepositoryCoordinates repository,
            ComparisonReference baseReference,
            ComparisonReference targetReference,
            List<String> errors
    ) {
        try {
            Optional<ApplicationConfigurationFile> base = sourceControlPort.readApplicationConfiguration(repository, baseReference);
            Optional<ApplicationConfigurationFile> target = sourceControlPort.readApplicationConfiguration(repository, targetReference);
            if (base.isEmpty() || target.isEmpty()) {
                return List.of();
            }
            return configurationDiffService.diff(target.get().path(), base.get().flattenedValues(), target.get().flattenedValues())
                    .stream()
                    .sorted(Comparator.comparing(ConfigurationDiff::key))
                    .toList();
        } catch (RuntimeException exception) {
            errors.add("Unable to compare application configuration: " + exception.getMessage());
            return List.of();
        }
    }

    private List<JiraIssueInfo> jiraIssues(List<CommitInfo> commits, List<String> errors) {
        Set<String> issueKeys = commits.stream()
                .flatMap(commit -> commit.jiraIssueKeys().stream())
                .collect(Collectors.toUnmodifiableSet());
        if (issueKeys.isEmpty()) {
            return List.of();
        }
        try {
            return issueTrackerPort.findIssuesByKeys(issueKeys).values().stream().toList();
        } catch (RuntimeException exception) {
            errors.add("Unable to read Jira issues: " + exception.getMessage());
            return List.of();
        }
    }

    private int modifiedFileCount(RepositoryCoordinates repository, List<CommitInfo> commits, List<String> errors) {
        int count = 0;
        for (CommitInfo commit : commits) {
            try {
                count += sourceControlPort.listChangedFiles(repository, commit.hash()).size();
            } catch (RuntimeException exception) {
                errors.add("Unable to count changed files for commit " + commit.hash() + ": " + exception.getMessage());
            }
        }
        return count;
    }

    public record ComparisonAnalysisRequest(List<ComparisonApplicationRequest> applications) {
        public ComparisonAnalysisRequest {
            applications = applications == null ? List.of() : List.copyOf(applications);
        }
    }

    public record ComparisonApplicationRequest(
            RepositoryCoordinates repository,
            ComparisonReference baseReference,
            ComparisonReference targetReference
    ) {
        public ComparisonApplicationRequest {
            Objects.requireNonNull(repository, "repository must not be null");
            Objects.requireNonNull(baseReference, "baseReference must not be null");
            Objects.requireNonNull(targetReference, "targetReference must not be null");
        }
    }

    public record ComparisonAnalysisResponse(
            ApplicationRepository repository,
            ComparisonReference baseReference,
            ComparisonReference targetReference,
            int commitCount,
            int modifiedFileCount,
            boolean hasConfigurationChanges,
            Set<String> jiraIssueKeys,
            List<CommitInfo> commits,
            List<JiraIssueInfo> jiraIssues,
            List<ConfigurationDiff> configurationDiffs,
            List<String> errors
    ) {
        static ComparisonAnalysisResponse from(
                ApplicationAnalysis analysis,
                ComparisonReference baseReference,
                ComparisonReference targetReference,
                int modifiedFileCount
        ) {
            return new ComparisonAnalysisResponse(
                    analysis.repository(),
                    baseReference,
                    targetReference,
                    analysis.commitCount(),
                    modifiedFileCount,
                    analysis.hasConfigurationChanges(),
                    analysis.jiraIssueKeys(),
                    analysis.commits(),
                    analysis.jiraIssues(),
                    analysis.configurationDiffs(),
                    analysis.errors()
            );
        }
    }
}
