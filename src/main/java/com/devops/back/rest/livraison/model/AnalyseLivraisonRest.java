package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnalyseLivraisonRest(
        String project,
        String slug,
        String statut,
        ResumeCommitsRest commits,
        @JsonProperty("conf") ConfigurationAnalyseRest conf,
        @JsonProperty("nbFichiersModifies") int nbFichiersModifies,
        List<String> jiras
) {

    public static AnalyseLivraisonRest depuisDomaine(AnalyseLivraison analyse) {
        return new AnalyseLivraisonRest(
                analyse.projet(),
                analyse.slug(),
                analyse.statut().name(),
                ResumeCommitsRest.depuisDomaine(analyse.commits()),
                ConfigurationAnalyseRest.depuisDomaine(analyse.configuration()),
                analyse.nombreFichiersModifies(),
                analyse.jiras()
        );
    }
}
