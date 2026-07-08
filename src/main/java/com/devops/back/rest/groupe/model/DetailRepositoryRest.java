package com.devops.back.rest.groupe.model;

import java.util.List;

public record DetailRepositoryRest(
        String project,
        String slug,
        String nom,
        List<VersionEnvironnementRest> versions
) {
}
