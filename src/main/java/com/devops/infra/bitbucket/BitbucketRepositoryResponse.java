package com.devops.infra.bitbucket;

record BitbucketRepositoryResponse(
        String slug,
        String name,
        String description,
        String url,
        BitbucketProjectResponse project
) {
}
