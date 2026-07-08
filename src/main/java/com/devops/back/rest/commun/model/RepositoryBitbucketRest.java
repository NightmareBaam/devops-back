package com.devops.back.rest.commun.model;

import com.devops.back.domain.commun.modele.RepositoryBitbucket;
import jakarta.validation.constraints.NotBlank;

public record RepositoryBitbucketRest(
        @NotBlank String project,
        @NotBlank String slug
) {

    public RepositoryBitbucket versDomaine() {
        return new RepositoryBitbucket(project, slug);
    }

    public static RepositoryBitbucketRest depuisDomaine(RepositoryBitbucket repositoryBitbucket) {
        return new RepositoryBitbucketRest(repositoryBitbucket.projet(), repositoryBitbucket.slug());
    }
}
