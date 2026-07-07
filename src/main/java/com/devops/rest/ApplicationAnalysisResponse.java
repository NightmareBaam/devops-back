package com.devops.rest;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.ConfigurationDiff;
import com.devops.domain.model.JiraIssueInfo;

import java.util.List;
import java.util.Set;

public record ApplicationAnalysisResponse(
        ApplicationRepository repository,
        ComparisonReference comparisonReference,
        int commitCount,
        boolean hasConfigurationChanges,
        Set<String> jiraIssueKeys,
        List<CommitInfo> commits,
        List<JiraIssueInfo> jiraIssues,
        List<ConfigurationDiff> configurationDiffs,
        List<String> errors
) {

    public static ApplicationAnalysisResponse from(ApplicationAnalysis analysis) {
        return new ApplicationAnalysisResponse(
                analysis.repository(),
                analysis.comparisonReference(),
                analysis.commitCount(),
                analysis.hasConfigurationChanges(),
                analysis.jiraIssueKeys(),
                analysis.commits(),
                analysis.jiraIssues(),
                analysis.configurationDiffs(),
                analysis.errors()
        );
    }
}
