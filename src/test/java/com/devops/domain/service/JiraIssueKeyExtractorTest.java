package com.devops.domain.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JiraIssueKeyExtractorTest {

    private final JiraIssueKeyExtractor extractor = new JiraIssueKeyExtractor();

    @Test
    void extractsDistinctJiraKeysFromCommitMessage() {
        Set<String> keys = extractor.extract("ABC-123 fix api, DEF-456 add tests, ABC-123 again");

        assertThat(keys).containsExactlyInAnyOrder("ABC-123", "DEF-456");
    }

    @Test
    void ignoresLowercaseOrIncompleteKeys() {
        Set<String> keys = extractor.extract("abc-123 missing project case, ABC- missing number, -123 missing project");

        assertThat(keys).isEmpty();
    }
}
