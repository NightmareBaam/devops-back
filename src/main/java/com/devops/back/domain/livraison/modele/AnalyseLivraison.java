package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.Listes;

import java.util.List;

public record AnalyseLivraison(
        String projet,
        String slug,
        StatutAnalyseLivraison statut,
        ResumeCommits commits,
        ConfigurationAnalyse configuration,
        int nombreFichiersModifies,
        List<String> jiras
) {

    public AnalyseLivraison {
        jiras = Listes.copie(jiras);
    }
}
