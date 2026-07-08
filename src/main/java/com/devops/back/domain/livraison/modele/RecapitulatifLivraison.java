package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.Listes;

import java.util.List;

public record RecapitulatifLivraison(
        List<AnalyseLivraison> analyses,
        List<String> jiras,
        int nombreApplicatifs,
        int nombreCommits,
        int nombreFichiersModifies,
        boolean configurationModifiee
) {

    public RecapitulatifLivraison {
        analyses = Listes.copie(analyses);
        jiras = Listes.copie(jiras);
    }
}
