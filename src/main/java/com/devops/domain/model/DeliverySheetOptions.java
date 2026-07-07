package com.devops.domain.model;

public record DeliverySheetOptions(
        boolean includeCommitLinks,
        boolean includeJiraBuildLinks,
        boolean includeBitbucketLinks
) {
}
