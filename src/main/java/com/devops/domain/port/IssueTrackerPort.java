package com.devops.domain.port;

import com.devops.domain.model.JiraIssueInfo;

import java.util.Map;
import java.util.Set;

public interface IssueTrackerPort {

    Map<String, JiraIssueInfo> findIssuesByKeys(Set<String> issueKeys);
}
