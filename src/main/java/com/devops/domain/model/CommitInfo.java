package com.devops.domain.model;

import java.time.Instant;
import java.util.Set;

public record CommitInfo(
        String hash,
        String message,
        String author,
        Instant date,
        Set<String> jiraIssueKeys,
        boolean configurationModified
) {

    public CommitInfo {
        hash = DomainValidation.notBlank(hash, "hash");
        message = message == null ? "" : message;
        author = author == null ? "" : author;
        jiraIssueKeys = jiraIssueKeys == null ? Set.of() : Set.copyOf(jiraIssueKeys);
    }

    public CommitInfo withJiraIssueKeys(Set<String> issueKeys) {
        return new CommitInfo(hash, message, author, date, issueKeys, configurationModified);
    }

    public CommitInfo withConfigurationModified(boolean modified) {
        return new CommitInfo(hash, message, author, date, jiraIssueKeys, modified);
    }
}
