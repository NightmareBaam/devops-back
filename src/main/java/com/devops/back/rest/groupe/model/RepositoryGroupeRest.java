package com.devops.back.rest.groupe.model;

import com.devops.back.domain.groupe.modele.RepositoryGroupe;
import jakarta.validation.constraints.NotBlank;

public record RepositoryGroupeRest(
        int order,
        @NotBlank String project,
        @NotBlank String slug
) {

    public RepositoryGroupe versDomaine() {
        return new RepositoryGroupe(order, project, slug);
    }

    public static RepositoryGroupeRest depuisDomaine(RepositoryGroupe repositoryGroupe) {
        return new RepositoryGroupeRest(repositoryGroupe.ordre(), repositoryGroupe.projet(), repositoryGroupe.slug());
    }
}
