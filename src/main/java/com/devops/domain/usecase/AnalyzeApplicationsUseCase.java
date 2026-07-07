package com.devops.domain.usecase;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.ComparisonReferenceType;
import com.devops.domain.model.ConfigurationDiff;
import com.devops.domain.model.DeliveryAnalysisRequest;
import com.devops.domain.model.DeliveryApplicationSelection;
import com.devops.domain.model.JiraIssueInfo;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.model.SourceFileChange;
import com.devops.domain.port.DeploymentConfigurationPort;
import com.devops.domain.port.IssueTrackerPort;
import com.devops.domain.port.SourceControlPort;
import com.devops.domain.service.ConfigurationChangeDetector;
import com.devops.domain.service.ConfigurationDiffService;
import com.devops.domain.service.JiraIssueKeyExtractor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AnalyzeApplicationsUseCase {

    private static final String DEFAULT_TARGET_BRANCH = "develop";

    private final SourceControlPort sourceControlPort;
    private final DeploymentConfigurationPort deploymentConfigurationPort;
    private final IssueTrackerPort issueTrackerPort;
    private final JiraIssueKeyExtractor jiraIssueKeyExtractor;
    private final ConfigurationChangeDetector configurationChangeDetector;
    private final ConfigurationDiffService configurationDiffService;
    private final ComparisonReference targetReference;

    public AnalyzeApplicationsUseCase(
            SourceControlPort sourceControlPort,
            DeploymentConfigurationPort deploymentConfigurationPort,
            IssueTrackerPort issueTrackerPort
    ) {
        this(
                sourceControlPort,
                deploymentConfigurationPort,
                issueTrackerPort,
                new JiraIssueKeyExtractor(),
                new ConfigurationChangeDetector(),
                new ConfigurationDiffService(),
                ComparisonReference.branch(DEFAULT_TARGET_BRANCH)
        );
    }

    public AnalyzeApplicationsUseCase(
            SourceControlPort sourceControlPort,
            DeploymentConfigurationPort deploymentConfigurationPort,
            IssueTrackerPort issueTrackerPort,
            JiraIssueKeyExtractor jiraIssueKeyExtractor,
            ConfigurationChangeDetector configurationChangeDetector,
            ConfigurationDiffService configurationDiffService,
            ComparisonReference targetReference
    ) {
        this.sourceControlPort = Objects.requireNonNull(sourceControlPort, "sourceControlPort must not be null");
        this.deploymentConfigurationPort = Objects.requireNonNull(deploymentConfigurationPort, "deploymentConfigurationPort must not be null");
        this.issueTrackerPort = Objects.requireNonNull(issueTrackerPort, "issueTrackerPort must not be null");
        this.jiraIssueKeyExtractor = Objects.requireNonNull(jiraIssueKeyExtractor, "jiraIssueKeyExtractor must not be null");
        this.configurationChangeDetector = Objects.requireNonNull(configurationChangeDetector, "configurationChangeDetector must not be null");
        this.configurationDiffService = Objects.requireNonNull(configurationDiffService, "configurationDiffService must not be null");
        this.targetReference = Objects.requireNonNull(targetReference, "targetReference must not be null");
    }

    public List<ApplicationAnalysis> analyze(DeliveryAnalysisRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return request.applications().stream()
                .map(this::analyzeApplication)
                .toList();
    }

    private ApplicationAnalysis analyzeApplication(DeliveryApplicationSelection selection) {
        RepositoryCoordinates repository = selection.repository().coordinates();
        List<String> errors = new ArrayList<>();
        List<CommitInfo> commits;
        List<ConfigurationDiff> configurationDiffs;

        try {
            ComparisonReference fromReference = resolveReference(repository, selection.comparisonReference());
            commits = enrichCommits(repository, sourceControlPort.listCommitsBetween(repository, fromReference, targetReference), errors);
            configurationDiffs = findConfigurationDiffs(repository, fromReference, errors);
        } catch (RuntimeException exception) {
            return new ApplicationAnalysis(
                    selection.repository(),
                    selection.comparisonReference(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(exception.getMessage())
            );
        }

        List<JiraIssueInfo> jiraIssues = findJiraIssues(commits, errors);

        return new ApplicationAnalysis(
                selection.repository(),
                selection.comparisonReference(),
                commits,
                jiraIssues,
                configurationDiffs,
                errors
        );
    }

    private ComparisonReference resolveReference(RepositoryCoordinates repository, ComparisonReference reference) {
        if (reference.type() != ComparisonReferenceType.ENVIRONMENT) {
            return reference;
        }

        RepositoryVersion deliveredVersion = deploymentConfigurationPort
                .findDeliveredVersion(repository, reference.environment())
                .orElseThrow(() -> new IllegalStateException(
                        "No delivered version found for " + repository.projectKey() + "/" + repository.slug()
                                + " in " + reference.environment()
                ));
        return ComparisonReference.tag(deliveredVersion.version());
    }

    private List<CommitInfo> enrichCommits(RepositoryCoordinates repository, List<CommitInfo> commits, List<String> errors) {
        if (commits == null || commits.isEmpty()) {
            return List.of();
        }

        return commits.stream()
                .map(commit -> enrichCommit(repository, commit, errors))
                .toList();
    }

    private CommitInfo enrichCommit(RepositoryCoordinates repository, CommitInfo commit, List<String> errors) {
        Set<String> jiraIssueKeys = jiraIssueKeyExtractor.extract(commit.message());
        boolean configurationModified = commit.configurationModified();

        try {
            List<SourceFileChange> changedFiles = sourceControlPort.listChangedFiles(repository, commit.hash());
            configurationModified = configurationModified
                    || configurationChangeDetector.hasApplicationConfigurationChange(changedFiles);
        } catch (RuntimeException exception) {
            errors.add("Unable to read changed files for commit " + commit.hash() + ": " + exception.getMessage());
        }

        return commit.withJiraIssueKeys(jiraIssueKeys).withConfigurationModified(configurationModified);
    }

    private List<ConfigurationDiff> findConfigurationDiffs(
            RepositoryCoordinates repository,
            ComparisonReference fromReference,
            List<String> errors
    ) {
        try {
            Optional<ApplicationConfigurationFile> before = sourceControlPort.readApplicationConfiguration(repository, fromReference);
            Optional<ApplicationConfigurationFile> after = sourceControlPort.readApplicationConfiguration(repository, targetReference);
            if (before.isEmpty() || after.isEmpty()) {
                return List.of();
            }
            return configurationDiffService
                    .diff(after.get().path(), before.get().flattenedValues(), after.get().flattenedValues())
                    .stream()
                    .sorted(Comparator.comparing(ConfigurationDiff::key))
                    .toList();
        } catch (RuntimeException exception) {
            errors.add("Unable to compare application configuration: " + exception.getMessage());
            return List.of();
        }
    }

    private List<JiraIssueInfo> findJiraIssues(List<CommitInfo> commits, List<String> errors) {
        Set<String> issueKeys = commits.stream()
                .flatMap(commit -> commit.jiraIssueKeys().stream())
                .collect(Collectors.toUnmodifiableSet());
        if (issueKeys.isEmpty()) {
            return List.of();
        }

        try {
            Map<String, JiraIssueInfo> issues = issueTrackerPort.findIssuesByKeys(issueKeys);
            return issueKeys.stream()
                    .map(issues::get)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (RuntimeException exception) {
            errors.add("Unable to read Jira issues: " + exception.getMessage());
            return List.of();
        }
    }
}
