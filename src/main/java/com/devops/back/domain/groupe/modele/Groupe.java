package com.devops.back.domain.groupe.modele;

import com.devops.back.domain.commun.modele.ReferenceVersion;

import com.devops.back.domain.commun.modele.Listes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Groupe(
        UUID identifiant,
        String libelle,
        String description,
        ReferenceVersion base,
        ReferenceVersion cible,
        List<RepositoryGroupe> repositoriesBitbucket,
        LocalDateTime dateDerniereModification
) {

    public Groupe {
        repositoriesBitbucket = Listes.copie(repositoriesBitbucket);
    }

    public int nombreApplicatifs() {
        return repositoriesBitbucket.size();
    }
}
