package com.devops.domain.service;

import com.devops.domain.model.CommitInfo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UnreleasedCommitSelectorTest {

    private final UnreleasedCommitSelector selector = new UnreleasedCommitSelector();

    @Test
    void keepsOnlyCommitsNotAlreadyDelivered() {
        CommitInfo delivered = commit("abc");
        CommitInfo unreleased = commit("def");

        List<CommitInfo> commits = selector.selectUnreleased(List.of(delivered, unreleased), Set.of("abc"));

        assertThat(commits).containsExactly(unreleased);
    }

    private static CommitInfo commit(String hash) {
        return new CommitInfo(hash, "message", "author", Instant.parse("2026-07-02T00:00:00Z"), Set.of(), false);
    }
}
