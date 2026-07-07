package com.devops.domain.model;

public record ConfluenceSelection(
        String spaceKey,
        String templateId,
        String visibility
) {
}
