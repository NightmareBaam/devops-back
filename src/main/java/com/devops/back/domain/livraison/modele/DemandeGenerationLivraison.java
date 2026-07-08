package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.ReferenceVersion;

import com.devops.back.domain.commun.modele.Listes;

import java.time.LocalDate;
import java.util.List;

public record DemandeGenerationLivraison(
        String libelle,
        LocalDate dateLivraison,
        String description,
        String espaceConfluence,
        List<ApplicatifAnalyse> applicatifs,
        ReferenceVersion cible
) {

    public DemandeGenerationLivraison {
        applicatifs = Listes.copie(applicatifs);
    }
}
