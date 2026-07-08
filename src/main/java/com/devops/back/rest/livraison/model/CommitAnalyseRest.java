package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.CommitAnalyse;

import java.util.List;

public record CommitAnalyseRest(
        String hash,
        String libelle,
        String auteur,
        List<String> jiras,
        boolean configurationModifiee
) {

    public static CommitAnalyseRest depuisDomaine(CommitAnalyse commitAnalyse) {
        return new CommitAnalyseRest(
                commitAnalyse.hash(),
                commitAnalyse.libelle(),
                commitAnalyse.auteur(),
                commitAnalyse.jiras(),
                commitAnalyse.configurationModifiee()
        );
    }
}
