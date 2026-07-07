package com.devops.domain.service;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.ConfigurationChangeType;
import com.devops.domain.model.ConfigurationDiff;
import com.devops.domain.model.DeliverySummary;
import com.devops.domain.model.JiraIssueInfo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DeliverySummaryPreparerTest {

    private final DeliverySummaryPreparer preparer = new DeliverySummaryPreparer(
            Clock.fixed(Instant.parse("2026-07-02T12:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void aggregatesCommitCountJiraKeysAndConfigurationFlag() {
        ApplicationAnalysis analysis = new ApplicationAnalysis(
                new ApplicationRepository("PRJ", "app", "Application", "https://bitbucket/app", "", List.of()),
                ComparisonReference.branch("develop"),
                List.of(new CommitInfo("abc", "ABC-123 change", "alice", Instant.parse("2026-07-02T00:00:00Z"), Set.of("ABC-123"), true)),
                List.of(new JiraIssueInfo("DEF-456", "Issue title", "Open", "bob", Set.of("delivery"))),
                List.of(new ConfigurationDiff("application.yml", "server.port", "8080", "9090", ConfigurationChangeType.MODIFIED)),
                List.of()
        );

        DeliverySummary summary = preparer.prepare(List.of(analysis));

        assertThat(summary.totalCommitCount()).isEqualTo(1);
        assertThat(summary.jiraIssueKeys()).containsExactlyInAnyOrder("ABC-123", "DEF-456");
        assertThat(summary.hasConfigurationChanges()).isTrue();
        assertThat(summary.preparedAt()).isEqualTo(Instant.parse("2026-07-02T12:00:00Z"));
    }
}
