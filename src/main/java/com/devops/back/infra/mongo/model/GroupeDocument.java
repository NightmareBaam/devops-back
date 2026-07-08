package com.devops.back.infra.mongo.model;

import com.devops.back.domain.commun.modele.ReferenceVersion;
import com.devops.back.domain.groupe.modele.RepositoryGroupe;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document("groupes")
public record GroupeDocument(
        @Id UUID identifiant,
        String libelle,
        String description,
        ReferenceVersion base,
        ReferenceVersion cible,
        List<RepositoryGroupe> repositoriesBitbucket,
        LocalDateTime dateDerniereModification
) {
}
