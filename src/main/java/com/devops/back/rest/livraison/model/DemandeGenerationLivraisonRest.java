package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.DemandeGenerationLivraison;
import com.devops.back.rest.commun.model.ReferenceVersionRest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record DemandeGenerationLivraisonRest(
        @NotBlank String libelle,
        @NotNull LocalDate dateLivraison,
        String description,
        @NotBlank String espaceConfluence,
        @NotEmpty List<@Valid ApplicatifAnalyseRest> applicatifs,
        @NotNull @Valid ReferenceVersionRest cible
) {

    public DemandeGenerationLivraison versDomaine() {
        return new DemandeGenerationLivraison(
                libelle,
                dateLivraison,
                description,
                espaceConfluence,
                applicatifs.stream().map(ApplicatifAnalyseRest::versDomaine).toList(),
                cible.versDomaine()
        );
    }
}
