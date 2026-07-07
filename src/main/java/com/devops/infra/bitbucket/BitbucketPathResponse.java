package com.devops.infra.bitbucket;

import java.util.List;

record BitbucketPathResponse(
        String name,
        List<String> components
) {

    String value() {
        if (components != null && !components.isEmpty()) {
            return String.join("/", components);
        }
        return name == null ? "" : name;
    }
}
