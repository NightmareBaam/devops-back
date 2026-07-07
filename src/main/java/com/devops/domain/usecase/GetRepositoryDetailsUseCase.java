package com.devops.domain.usecase;

import com.devops.domain.exception.RepositoryNotFoundException;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.port.RepositoryCatalogPort;

import java.util.List;
import java.util.Objects;

public class GetRepositoryDetailsUseCase {

    private final RepositoryCatalogPort repositoryCatalogPort;

    public GetRepositoryDetailsUseCase(RepositoryCatalogPort repositoryCatalogPort) {
        this.repositoryCatalogPort = Objects.requireNonNull(repositoryCatalogPort, "repositoryCatalogPort must not be null");
    }

    public ApplicationRepository getDetails(RepositoryCoordinates repository) {
        Objects.requireNonNull(repository, "repository must not be null");

        ApplicationRepository foundRepository = repositoryCatalogPort.findRepository(repository)
                .orElseThrow(() -> new RepositoryNotFoundException(repository));
        List<RepositoryVersion> versions = repositoryCatalogPort.findVersions(repository);

        return new ApplicationRepository(
                foundRepository.projectKey(),
                foundRepository.slug(),
                foundRepository.name(),
                foundRepository.url(),
                foundRepository.description(),
                versions
        );
    }
}
