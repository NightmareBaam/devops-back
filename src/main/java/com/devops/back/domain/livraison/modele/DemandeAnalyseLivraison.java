package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.ReferenceVersion;

import com.devops.back.domain.commun.modele.Listes;

import java.util.List;

public record DemandeAnalyseLivraison(
        List<ApplicatifAnalyse> applicatifs,
        ReferenceVersion cible
) {

    public DemandeAnalyseLivraison {
        applicatifs = Listes.copie(applicatifs);
    }
}
