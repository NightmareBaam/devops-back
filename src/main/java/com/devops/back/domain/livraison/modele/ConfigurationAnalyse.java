package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.Listes;

import java.util.List;

public record ConfigurationAnalyse(
        boolean modifiee,
        List<ModificationConfiguration> modifications
) {

    public ConfigurationAnalyse {
        modifications = Listes.copie(modifications);
    }
}
