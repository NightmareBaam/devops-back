package com.devops.infra.bitbucket;

record BitbucketCommitResponse(
        String id,
        String displayId,
        String message,
        String authorName,
        Long authorTimestamp
) {
}
