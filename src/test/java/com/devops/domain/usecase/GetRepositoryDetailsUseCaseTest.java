package com.devops.domain.usecase;

import com.devops.domain.exception.RepositoryNotFoundException;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.Environment;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.port.RepositoryCatalogPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRepositoryDetailsUseCaseTest {

    @Test
    void returnsRepositoryWithDeliveredVersions() {
        RepositoryVersion recVersion = new RepositoryVersion(Environment.REC, "1.2.3", null);
        RepositoryCatalogPort port = new RepositoryCatalogPort() {
            @Override
            public List<ApplicationRepository> searchRepositories(String query, int limit) {
                return List.of();
            }

            @Override
            public Optional<ApplicationRepository> findRepository(RepositoryCoordinates repository) {
                return Optional.of(new ApplicationRepository("PRJ", "app", "Application", "url", "description", List.of()));
            }

            @Override
            public List<RepositoryVersion> findVersions(RepositoryCoordinates repository) {
                return List.of(recVersion);
            }
        };

        ApplicationRepository repository = new GetRepositoryDetailsUseCase(port)
                .getDetails(new RepositoryCoordinates("PRJ", "app"));

        assertThat(repository.versions()).containsExactly(recVersion);
    }

    @Test
    void failsWhenRepositoryDoesNotExist() {
        RepositoryCatalogPort port = new EmptyRepositoryCatalogPort();
        GetRepositoryDetailsUseCase useCase = new GetRepositoryDetailsUseCase(port);

        assertThatThrownBy(() -> useCase.getDetails(new RepositoryCoordinates("PRJ", "missing")))
                .isInstanceOf(RepositoryNotFoundException.class)
                .hasMessage("Repository not found: PRJ/missing");
    }

    private static class EmptyRepositoryCatalogPort implements RepositoryCatalogPort {
        @Override
        public List<ApplicationRepository> searchRepositories(String query, int limit) {
            return List.of();
        }

        @Override
        public Optional<ApplicationRepository> findRepository(RepositoryCoordinates repository) {
            return Optional.empty();
        }

        @Override
        public List<RepositoryVersion> findVersions(RepositoryCoordinates repository) {
            return List.of();
        }
    }
}
