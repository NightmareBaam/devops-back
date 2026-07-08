package com.devops.back.infra.confluence.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LiensConfluenceRest(String webui) {
}
