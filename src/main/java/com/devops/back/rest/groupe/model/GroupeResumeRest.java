package com.devops.back.rest.groupe.model;

import com.devops.back.domain.groupe.modele.Groupe;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupeResumeRest(
        UUID id,
        String libelle,
        int countApps,
        LocalDateTime dateDerniereModification
) {

    public static GroupeResumeRest depuisDomaine(Groupe groupe) {
        return new GroupeResumeRest(
                groupe.identifiant(),
                groupe.libelle(),
                groupe.nombreApplicatifs(),
                groupe.dateDerniereModification()
        );
    }
}
