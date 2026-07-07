package com.devops.infra.bitbucket;

record BitbucketReferenceResponse(
        String id,
        String displayId,
        String latestCommit
) {
}
