package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.RecapitulatifLivraison;

import java.util.List;

public record RecapitulatifLivraisonRest(
        int nombreApplicatifs,
        int nombreCommits,
        int nombreFichiersModifies,
        boolean configurationModifiee,
        List<String> jiras,
        List<AnalyseLivraisonRest> analyses
) {

    public static RecapitulatifLivraisonRest depuisDomaine(RecapitulatifLivraison recapitulatif) {
        return new RecapitulatifLivraisonRest(
                recapitulatif.nombreApplicatifs(),
                recapitulatif.nombreCommits(),
                recapitulatif.nombreFichiersModifies(),
                recapitulatif.configurationModifiee(),
                recapitulatif.jiras(),
                recapitulatif.analyses().stream().map(AnalyseLivraisonRest::depuisDomaine).toList()
        );
    }
}
