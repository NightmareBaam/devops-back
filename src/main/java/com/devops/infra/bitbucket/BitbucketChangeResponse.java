package com.devops.infra.bitbucket;

record BitbucketChangeResponse(
        BitbucketPathResponse path,
        String type
) {
}
