package com.devops.infra.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "devops.deployment-configuration")
public record DeploymentConfigurationProperties(
        Map<String, ApplicationVersion> rec,
        Map<String, ApplicationVersion> ppr,
        Map<String, ApplicationVersion> prod
) {

    public DeploymentConfigurationProperties {
        rec = rec == null ? Map.of() : Map.copyOf(rec);
        ppr = ppr == null ? Map.of() : Map.copyOf(ppr);
        prod = prod == null ? Map.of() : Map.copyOf(prod);
    }

    public record ApplicationVersion(
            String projectKey,
            String slug,
            String version
    ) {
    }
}
