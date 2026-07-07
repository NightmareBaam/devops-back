package com.devops.domain.service;

import com.devops.domain.model.CommitInfo;

import java.util.List;
import java.util.Set;

public class UnreleasedCommitSelector {

    public List<CommitInfo> selectUnreleased(List<CommitInfo> candidateCommits, Set<String> deliveredCommitHashes) {
        if (candidateCommits == null || candidateCommits.isEmpty()) {
            return List.of();
        }
        Set<String> deliveredHashes = deliveredCommitHashes == null ? Set.of() : deliveredCommitHashes;
        return candidateCommits.stream()
                .filter(commit -> !deliveredHashes.contains(commit.hash()))
                .toList();
    }
}
