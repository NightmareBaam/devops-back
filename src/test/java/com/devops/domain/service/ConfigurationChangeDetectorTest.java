package com.devops.domain.service;

import com.devops.domain.model.SourceFileChange;
import com.devops.domain.model.SourceFileChangeType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationChangeDetectorTest {

    private final ConfigurationChangeDetector detector = new ConfigurationChangeDetector();

    @Test
    void detectsApplicationYamlInAnyFolder() {
        boolean hasConfigurationChange = detector.hasApplicationConfigurationChange(List.of(
                new SourceFileChange("src/main/resources/application.yml", SourceFileChangeType.MODIFIED)
        ));

        assertThat(hasConfigurationChange).isTrue();
    }

    @Test
    void detectsApplicationYamlWithWindowsSeparator() {
        boolean hasConfigurationChange = detector.isApplicationConfigurationFile("config\\application.yaml");

        assertThat(hasConfigurationChange).isTrue();
    }

    @Test
    void ignoresOtherYamlFiles() {
        boolean hasConfigurationChange = detector.hasApplicationConfigurationChange(List.of(
                new SourceFileChange("src/main/resources/bootstrap.yml", SourceFileChangeType.MODIFIED)
        ));

        assertThat(hasConfigurationChange).isFalse();
    }
}
