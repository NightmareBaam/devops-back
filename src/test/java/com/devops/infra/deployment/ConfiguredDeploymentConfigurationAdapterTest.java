package com.devops.infra.deployment;

import com.devops.domain.model.Environment;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.infra.config.DeploymentConfigurationProperties;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ConfiguredDeploymentConfigurationAdapterTest {

    @Test
    void mapsConfiguredVersionsByEnvironment() {
        ConfiguredDeploymentConfigurationAdapter adapter = new ConfiguredDeploymentConfigurationAdapter(new DeploymentConfigurationProperties(
                Map.of("app", new DeploymentConfigurationProperties.ApplicationVersion("PRJ", "app", "1.0.0")),
                Map.of("app", new DeploymentConfigurationProperties.ApplicationVersion("PRJ", "app", "1.1.0")),
                Map.of()
        ));
        RepositoryCoordinates repository = new RepositoryCoordinates("PRJ", "app");

        List<RepositoryVersion> versions = adapter.findDeliveredVersions(repository);
        Optional<RepositoryVersion> recVersion = adapter.findDeliveredVersion(repository, Environment.REC);

        assertThat(versions).extracting(RepositoryVersion::version).containsExactlyInAnyOrder("1.0.0", "1.1.0");
        assertThat(recVersion).contains(new RepositoryVersion(Environment.REC, "1.0.0", null));
    }
}
