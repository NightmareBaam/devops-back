package com.devops.back.rest.groupe.model;

import com.devops.back.domain.groupe.modele.Groupe;
import com.devops.back.rest.commun.model.ReferenceVersionRest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record DemandeGroupeRest(
        @NotBlank String libelle,
        String description,
        @Valid ReferenceVersionRest base,
        @Valid ReferenceVersionRest cible,
        @NotEmpty @JsonProperty("repoBitBucket") List<@Valid RepositoryGroupeRest> repoBitBucket
) {

    public Groupe versDomaine(UUID id) {
        return new Groupe(
                id,
                libelle,
                description,
                base.versDomaine(),
                cible.versDomaine(),
                repoBitBucket.stream().map(RepositoryGroupeRest::versDomaine).toList(),
                null
        );
    }
}
