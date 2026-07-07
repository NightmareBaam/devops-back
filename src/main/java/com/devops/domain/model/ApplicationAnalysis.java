package com.devops.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ApplicationAnalysis(
        ApplicationRepository repository,
        ComparisonReference comparisonReference,
        List<CommitInfo> commits,
        List<JiraIssueInfo> jiraIssues,
        List<ConfigurationDiff> configurationDiffs,
        List<String> errors
) {

    public ApplicationAnalysis {
        repository = Objects.requireNonNull(repository, "repository must not be null");
        comparisonReference = Objects.requireNonNull(comparisonReference, "comparisonReference must not be null");
        commits = commits == null ? List.of() : List.copyOf(commits);
        jiraIssues = jiraIssues == null ? List.of() : List.copyOf(jiraIssues);
        configurationDiffs = configurationDiffs == null ? List.of() : List.copyOf(configurationDiffs);
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public int commitCount() {
        return commits.size();
    }

    public boolean hasConfigurationChanges() {
        return commits.stream().anyMatch(CommitInfo::configurationModified) || !configurationDiffs.isEmpty();
    }

    public Set<String> jiraIssueKeys() {
        return Stream.concat(
                        commits.stream().flatMap(commit -> commit.jiraIssueKeys().stream()),
                        jiraIssues.stream().map(JiraIssueInfo::key)
                )
                .collect(Collectors.toUnmodifiableSet());
    }
}
