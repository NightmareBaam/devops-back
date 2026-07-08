package com.devops.back.rest.groupe.model;

import com.devops.back.domain.groupe.modele.Groupe;
import com.devops.back.rest.commun.model.ReferenceVersionRest;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GroupeDetailRest(
        UUID id,
        String libelle,
        String description,
        int countApps,
        LocalDateTime dateDerniereModification,
        ReferenceVersionRest base,
        ReferenceVersionRest cible,
        @JsonProperty("repoBitBucket") List<RepositoryGroupeRest> repoBitBucket
) {

    public static GroupeDetailRest depuisDomaine(Groupe groupe) {
        return new GroupeDetailRest(
                groupe.identifiant(),
                groupe.libelle(),
                groupe.description(),
                groupe.nombreApplicatifs(),
                groupe.dateDerniereModification(),
                ReferenceVersionRest.depuisDomaine(groupe.base()),
                ReferenceVersionRest.depuisDomaine(groupe.cible()),
                groupe.repositoriesBitbucket().stream()
                        .map(RepositoryGroupeRest::depuisDomaine)
                        .toList()
        );
    }
}
