package com.devops.rest.controller;

import com.devops.domain.usecase.AddJiraIssueLabelsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/jira/issues")
public class JiraIssueController {

    private final AddJiraIssueLabelsUseCase useCase;

    public JiraIssueController(AddJiraIssueLabelsUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/labels")
    public ResponseEntity<Void> addLabels(@RequestBody JiraLabelsRequest request) {
        useCase.addLabels(Set.copyOf(request.issueKeys()), Set.copyOf(request.labels()));
        return ResponseEntity.noContent().build();
    }

    public record JiraLabelsRequest(
            List<String> issueKeys,
            List<String> labels
    ) {
        public JiraLabelsRequest {
            issueKeys = issueKeys == null ? List.of() : List.copyOf(issueKeys);
            labels = labels == null ? List.of() : List.copyOf(labels);
        }
    }
}
