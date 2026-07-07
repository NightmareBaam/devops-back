package com.devops.rest.controller;

import com.devops.domain.model.Environment;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.model.SourceReference;
import com.devops.domain.port.DeploymentConfigurationPort;
import com.devops.domain.port.SourceControlPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/repositories/{projectKey}/{repositorySlug}/references")
public class RepositoryReferencesController {

    private final SourceControlPort sourceControlPort;
    private final DeploymentConfigurationPort deploymentConfigurationPort;

    public RepositoryReferencesController(
            SourceControlPort sourceControlPort,
            DeploymentConfigurationPort deploymentConfigurationPort
    ) {
        this.sourceControlPort = sourceControlPort;
        this.deploymentConfigurationPort = deploymentConfigurationPort;
    }

    @GetMapping
    public ResponseEntity<RepositoryReferencesResponse> getReferences(
            @PathVariable String projectKey,
            @PathVariable String repositorySlug
    ) {
        RepositoryCoordinates repository = new RepositoryCoordinates(projectKey, repositorySlug);
        List<String> branches = sourceControlPort.listBranches(repository).stream()
                .map(SourceReference::name)
                .toList();
        List<String> tags = sourceControlPort.listTags(repository).stream()
                .map(SourceReference::name)
                .toList();
        List<EnvironmentReferenceResponse> environments = deploymentConfigurationPort.findDeliveredVersions(repository).stream()
                .map(EnvironmentReferenceResponse::from)
                .toList();

        return ResponseEntity.ok(new RepositoryReferencesResponse(repository, branches, tags, environments));
    }

    public record RepositoryReferencesResponse(
            RepositoryCoordinates repository,
            List<String> branches,
            List<String> tags,
            List<EnvironmentReferenceResponse> environments
    ) {
    }

    public record EnvironmentReferenceResponse(
            Environment environment,
            String version,
            Instant deliveredAt
    ) {

        static EnvironmentReferenceResponse from(RepositoryVersion version) {
            return new EnvironmentReferenceResponse(version.environment(), version.version(), version.deliveredAt());
        }
    }
}
