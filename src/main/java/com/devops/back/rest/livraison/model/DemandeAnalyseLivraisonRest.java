package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.DemandeAnalyseLivraison;
import com.devops.back.rest.commun.model.ReferenceVersionRest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DemandeAnalyseLivraisonRest(
        @NotEmpty List<@Valid ApplicatifAnalyseRest> applicatifs,
        @NotNull @Valid ReferenceVersionRest cible
) {

    public DemandeAnalyseLivraison versDomaine() {
        return new DemandeAnalyseLivraison(
                applicatifs.stream().map(ApplicatifAnalyseRest::versDomaine).toList(),
                cible.versDomaine()
        );
    }
}
