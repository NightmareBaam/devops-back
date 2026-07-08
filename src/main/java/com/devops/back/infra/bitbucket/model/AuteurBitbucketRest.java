package com.devops.back.infra.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuteurBitbucketRest(UtilisateurBitbucketRest user) {
}
