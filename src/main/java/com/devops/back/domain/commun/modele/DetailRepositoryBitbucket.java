package com.devops.back.domain.commun.modele;

import java.util.List;

public record DetailRepositoryBitbucket(
        String projet,
        String slug,
        String nom,
        List<VersionEnvironnement> versions
) {

    public DetailRepositoryBitbucket {
        versions = Listes.copie(versions);
    }
}
