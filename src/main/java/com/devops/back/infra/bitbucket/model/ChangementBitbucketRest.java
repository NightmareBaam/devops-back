package com.devops.back.infra.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChangementBitbucketRest(CheminBitbucketRest path) {

    public String chemin() {
        return path == null ? "" : path.chemin();
    }
}
