package com.devops.back.domain.livraison.casusage;

import com.devops.back.domain.commun.casusage.ValidationCasUsage;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.devops.back.domain.livraison.modele.RecapitulatifLivraison;

import java.util.LinkedHashSet;
import java.util.List;

public class GenererRecapitulatifLivraison {

    public RecapitulatifLivraison executer(List<AnalyseLivraison> analyses) {
        ValidationCasUsage.collectionObligatoire(analyses, "analyses");

        var jiras = new LinkedHashSet<String>();
        var nombreCommits = 0;
        var nombreFichiersModifies = 0;
        var configurationModifiee = false;

        for (AnalyseLivraison analyse : analyses) {
            if (analyse.jiras() != null) {
                jiras.addAll(analyse.jiras());
            }
            if (analyse.commits() != null) {
                nombreCommits += analyse.commits().nombre();
            }
            nombreFichiersModifies += analyse.nombreFichiersModifies();
            configurationModifiee = configurationModifiee
                    || (analyse.configuration() != null && analyse.configuration().modifiee());
        }

        return new RecapitulatifLivraison(
                analyses,
                List.copyOf(jiras),
                analyses.size(),
                nombreCommits,
                nombreFichiersModifies,
                configurationModifiee
        );
    }
}
