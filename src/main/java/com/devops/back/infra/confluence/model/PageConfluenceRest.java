package com.devops.back.infra.confluence.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PageConfluenceRest(String id, LiensConfluenceRest links) {
}
