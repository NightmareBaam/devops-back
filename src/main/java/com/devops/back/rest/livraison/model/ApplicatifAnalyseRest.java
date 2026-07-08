package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.ApplicatifAnalyse;
import com.devops.back.rest.commun.model.ReferenceVersionRest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicatifAnalyseRest(
        @NotBlank String project,
        @NotBlank String slug,
        @NotNull @Valid ReferenceVersionRest base
) {

    public ApplicatifAnalyse versDomaine() {
        return new ApplicatifAnalyse(project, slug, base.versDomaine());
    }
}
