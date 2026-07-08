package com.devops.back.infra.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitBitbucketRest(String displayId, String message, AuteurBitbucketRest author) {

    public String auteur() {
        return author == null || author.user() == null ? null : author.user().displayName();
    }
}
