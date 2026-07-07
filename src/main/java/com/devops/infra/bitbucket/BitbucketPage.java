package com.devops.infra.bitbucket;

import java.util.List;

record BitbucketPage<T>(
        List<T> values
) {
}
