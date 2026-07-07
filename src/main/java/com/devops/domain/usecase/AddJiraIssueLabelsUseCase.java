package com.devops.domain.usecase;

import com.devops.domain.port.IssueLabelPort;

import java.util.Set;

public class AddJiraIssueLabelsUseCase {

    private final IssueLabelPort issueLabelPort;

    public AddJiraIssueLabelsUseCase(IssueLabelPort issueLabelPort) {
        this.issueLabelPort = issueLabelPort;
    }

    public void addLabels(Set<String> issueKeys, Set<String> labels) {
        issueLabelPort.addLabels(issueKeys, labels);
    }
}
