package com.devops.domain.usecase;

import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.port.RepositoryCatalogPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchRepositoriesUseCaseTest {

    @Test
    void trimsQueryAndCapsLimit() {
        CapturingRepositoryCatalogPort port = new CapturingRepositoryCatalogPort();
        SearchRepositoriesUseCase useCase = new SearchRepositoriesUseCase(port);

        useCase.search("  app  ", 500);

        assertThat(port.query).isEqualTo("app");
        assertThat(port.limit).isEqualTo(SearchRepositoriesUseCase.MAX_LIMIT);
    }

    @Test
    void rejectsBlankQuery() {
        SearchRepositoriesUseCase useCase = new SearchRepositoriesUseCase(new CapturingRepositoryCatalogPort());

        assertThatThrownBy(() -> useCase.search(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("query must not be blank");
    }

    private static class CapturingRepositoryCatalogPort implements RepositoryCatalogPort {
        private String query;
        private int limit;

        @Override
        public List<ApplicationRepository> searchRepositories(String query, int limit) {
            this.query = query;
            this.limit = limit;
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
