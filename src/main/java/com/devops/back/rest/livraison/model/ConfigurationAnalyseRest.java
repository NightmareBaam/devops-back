package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.ConfigurationAnalyse;

import java.util.List;

public record ConfigurationAnalyseRest(
        boolean modifie,
        List<ModificationConfigurationRest> modifications
) {

    public static ConfigurationAnalyseRest depuisDomaine(ConfigurationAnalyse configurationAnalyse) {
        if (configurationAnalyse == null) {
            return new ConfigurationAnalyseRest(false, List.of());
        }
        return new ConfigurationAnalyseRest(
                configurationAnalyse.modifiee(),
                configurationAnalyse.modifications().stream()
                        .map(ModificationConfigurationRest::depuisDomaine)
                        .toList()
        );
    }
}
