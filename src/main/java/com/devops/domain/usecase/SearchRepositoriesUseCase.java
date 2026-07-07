package com.devops.domain.usecase;

import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.port.RepositoryCatalogPort;

import java.util.List;
import java.util.Objects;

public class SearchRepositoriesUseCase {

    public static final int DEFAULT_LIMIT = 25;
    public static final int MAX_LIMIT = 100;

    private final RepositoryCatalogPort repositoryCatalogPort;

    public SearchRepositoriesUseCase(RepositoryCatalogPort repositoryCatalogPort) {
        this.repositoryCatalogPort = Objects.requireNonNull(repositoryCatalogPort, "repositoryCatalogPort must not be null");
    }

    public List<ApplicationRepository> search(String query) {
        return search(query, DEFAULT_LIMIT);
    }

    public List<ApplicationRepository> search(String query, int limit) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }
        int effectiveLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
        return repositoryCatalogPort.searchRepositories(query.trim(), effectiveLimit);
    }
}
