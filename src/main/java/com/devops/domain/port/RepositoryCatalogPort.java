package com.devops.domain.port;

import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;

import java.util.List;
import java.util.Optional;

public interface RepositoryCatalogPort {

    List<ApplicationRepository> searchRepositories(String query, int limit);

    Optional<ApplicationRepository> findRepository(RepositoryCoordinates repository);

    List<RepositoryVersion> findVersions(RepositoryCoordinates repository);
}
