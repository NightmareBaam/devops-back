package com.devops.domain.usecase;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.DeliverySummary;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PrepareDeliverySummaryUseCaseTest {

    @Test
    void delegatesSummaryPreparation() {
        ApplicationAnalysis analysis = new ApplicationAnalysis(
                new ApplicationRepository("PRJ", "app", "Application", "url", "", List.of()),
                ComparisonReference.branch("develop"),
                List.of(new CommitInfo("abc", "ABC-123 commit", "alice", Instant.parse("2026-07-02T00:00:00Z"), Set.of("ABC-123"), false)),
                List.of(),
                List.of(),
                List.of()
        );

        DeliverySummary summary = new PrepareDeliverySummaryUseCase().prepare(List.of(analysis));

        assertThat(summary.totalCommitCount()).isEqualTo(1);
        assertThat(summary.jiraIssueKeys()).containsExactly("ABC-123");
    }
}
