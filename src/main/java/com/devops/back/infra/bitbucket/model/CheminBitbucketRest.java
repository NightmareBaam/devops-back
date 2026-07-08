package com.devops.back.infra.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheminBitbucketRest(List<String> components, @JsonProperty("toString") String cheminTexte) {

    public String chemin() {
        if (cheminTexte != null && !cheminTexte.isBlank()) {
            return cheminTexte;
        }
        return components == null ? "" : String.join("/", components);
    }
}
