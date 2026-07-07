package com.devops.domain.service;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.DeliverySummary;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeliverySummaryPreparer {

    private final Clock clock;

    public DeliverySummaryPreparer() {
        this(Clock.systemUTC());
    }

    public DeliverySummaryPreparer(Clock clock) {
        this.clock = clock;
    }

    public DeliverySummary prepare(List<ApplicationAnalysis> analyses) {
        List<ApplicationAnalysis> applications = analyses == null ? List.of() : List.copyOf(analyses);
        Set<String> jiraIssueKeys = applications.stream()
                .flatMap(analysis -> analysis.jiraIssueKeys().stream())
                .collect(Collectors.toUnmodifiableSet());
        int totalCommitCount = applications.stream()
                .mapToInt(ApplicationAnalysis::commitCount)
                .sum();
        boolean hasConfigurationChanges = applications.stream()
                .anyMatch(ApplicationAnalysis::hasConfigurationChanges);

        return new DeliverySummary(
                applications,
                jiraIssueKeys,
                totalCommitCount,
                hasConfigurationChanges,
                Instant.now(clock)
        );
    }
}
