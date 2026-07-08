package com.devops.back.infra.bitbucket.model;

public record ApplicationDeployee(
        String projet,
        String slug,
        String version,
        String cheminConfiguration
) {
}
