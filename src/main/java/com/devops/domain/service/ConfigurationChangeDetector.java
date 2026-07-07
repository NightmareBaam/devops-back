package com.devops.domain.service;

import com.devops.domain.model.SourceFileChange;

import java.util.Collection;

public class ConfigurationChangeDetector {

    public boolean hasApplicationConfigurationChange(Collection<SourceFileChange> changedFiles) {
        if (changedFiles == null || changedFiles.isEmpty()) {
            return false;
        }
        return changedFiles.stream().anyMatch(change -> isApplicationConfigurationFile(change.path()));
    }

    public boolean isApplicationConfigurationFile(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        String normalizedPath = path.replace('\\', '/').toLowerCase();
        int lastSeparator = normalizedPath.lastIndexOf('/');
        String fileName = lastSeparator >= 0 ? normalizedPath.substring(lastSeparator + 1) : normalizedPath;
        return "application.yml".equals(fileName) || "application.yaml".equals(fileName);
    }
}
