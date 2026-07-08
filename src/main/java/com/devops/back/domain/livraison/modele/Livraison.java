package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.ReferenceVersion;

import com.devops.back.domain.commun.modele.Listes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Livraison(
        UUID identifiant,
        String libelle,
        LocalDate dateLivraison,
        String description,
        ReferenceVersion cible,
        List<ApplicatifAnalyse> applicatifs,
        List<AnalyseLivraison> analyses,
        LocalDateTime dateCreation
) {

    public Livraison {
        applicatifs = Listes.copie(applicatifs);
        analyses = Listes.copie(analyses);
    }
}
