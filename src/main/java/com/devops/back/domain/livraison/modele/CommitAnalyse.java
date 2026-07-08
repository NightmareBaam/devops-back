package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.Listes;

import java.util.List;

public record CommitAnalyse(
        String hash,
        String libelle,
        String auteur,
        List<String> jiras,
        boolean configurationModifiee
) {

    public CommitAnalyse {
        jiras = Listes.copie(jiras);
    }
}
