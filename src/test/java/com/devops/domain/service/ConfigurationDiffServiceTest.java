package com.devops.domain.service;

import com.devops.domain.model.ConfigurationChangeType;
import com.devops.domain.model.ConfigurationDiff;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationDiffServiceTest {

    private final ConfigurationDiffService diffService = new ConfigurationDiffService();

    @Test
    void detectsAddedModifiedAndRemovedConfigurationValues() {
        Set<ConfigurationDiff> diffs = diffService.diff(
                "application.yml",
                Map.of(
                        "server.port", "8080",
                        "feature.old", "true",
                        "unchanged", "same"
                ),
                Map.of(
                        "server.port", "9090",
                        "feature.new", "enabled",
                        "unchanged", "same"
                )
        );

        assertThat(diffs).containsExactlyInAnyOrder(
                new ConfigurationDiff("application.yml", "server.port", "8080", "9090", ConfigurationChangeType.MODIFIED),
                new ConfigurationDiff("application.yml", "feature.old", "true", "", ConfigurationChangeType.REMOVED),
                new ConfigurationDiff("application.yml", "feature.new", "", "enabled", ConfigurationChangeType.ADDED)
        );
    }
}
