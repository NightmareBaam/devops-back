package com.devops.domain.service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraIssueKeyExtractor {

    private static final Pattern JIRA_ISSUE_KEY = Pattern.compile("\\b[A-Z]+-[0-9]+\\b");

    public Set<String> extract(String commitMessage) {
        if (commitMessage == null || commitMessage.isBlank()) {
            return Set.of();
        }

        Matcher matcher = JIRA_ISSUE_KEY.matcher(commitMessage);
        Set<String> keys = new LinkedHashSet<>();
        while (matcher.find()) {
            keys.add(matcher.group());
        }
        return Set.copyOf(keys);
    }
}
