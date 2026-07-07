package com.devops.infra.deployment;

import com.devops.domain.model.DeploymentApplicationVersion;
import com.devops.domain.model.Environment;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.port.DeploymentConfigurationPort;
import com.devops.infra.config.DeploymentConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ConfiguredDeploymentConfigurationAdapter implements DeploymentConfigurationPort {

    private final Map<Environment, List<DeploymentApplicationVersion>> versionsByEnvironment;

    public ConfiguredDeploymentConfigurationAdapter(DeploymentConfigurationProperties properties) {
        this.versionsByEnvironment = Map.copyOf(loadVersions(properties));
    }

    @Override
    public Map<Environment, List<DeploymentApplicationVersion>> findDeliveredApplicationsByEnvironment() {
        return versionsByEnvironment;
    }

    @Override
    public List<RepositoryVersion> findDeliveredVersions(RepositoryCoordinates repository) {
        return versionsByEnvironment.values().stream()
                .flatMap(List::stream)
                .filter(version -> version.repository().equals(repository))
                .map(DeploymentApplicationVersion::toRepositoryVersion)
                .toList();
    }

    @Override
    public Optional<RepositoryVersion> findDeliveredVersion(RepositoryCoordinates repository, Environment environment) {
        return versionsByEnvironment.getOrDefault(environment, List.of()).stream()
                .filter(version -> version.repository().equals(repository))
                .findFirst()
                .map(DeploymentApplicationVersion::toRepositoryVersion);
    }

    private Map<Environment, List<DeploymentApplicationVersion>> loadVersions(DeploymentConfigurationProperties properties) {
        Map<Environment, List<DeploymentApplicationVersion>> versions = new EnumMap<>(Environment.class);
        versions.put(Environment.REC, toVersions(Environment.REC, properties.rec()));
        versions.put(Environment.PPR, toVersions(Environment.PPR, properties.ppr()));
        versions.put(Environment.PROD, toVersions(Environment.PROD, properties.prod()));
        versions.put(Environment.DEV, List.of());
        return versions;
    }

    private List<DeploymentApplicationVersion> toVersions(
            Environment environment,
            Map<String, DeploymentConfigurationProperties.ApplicationVersion> applications
    ) {
        return applications.values().stream()
                .map(application -> new DeploymentApplicationVersion(
                        new RepositoryCoordinates(application.projectKey(), application.slug()),
                        environment,
                        application.version(),
                        null
                ))
                .toList();
    }
}
