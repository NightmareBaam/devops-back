package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.ModificationConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ModificationConfigurationRest(
        String key,
        @JsonProperty("oldvalue") Object oldvalue,
        Object newValue
) {

    public static ModificationConfigurationRest depuisDomaine(ModificationConfiguration modificationConfiguration) {
        return new ModificationConfigurationRest(
                modificationConfiguration.cle(),
                modificationConfiguration.ancienneValeur(),
                modificationConfiguration.nouvelleValeur()
        );
    }
}
