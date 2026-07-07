package com.devops.rest;

import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.Environment;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.port.RepositoryCatalogPort;
import com.devops.domain.usecase.GetRepositoryDetailsUseCase;
import com.devops.domain.usecase.SearchRepositoriesUseCase;
import com.devops.rest.controller.RepositoryController;
import com.devops.rest.error.CorrelationIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RepositoryControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RepositoryCatalogPort repositoryCatalogPort = new StubRepositoryCatalogPort();
        RepositoryController controller = new RepositoryController(
                new SearchRepositoriesUseCase(repositoryCatalogPort),
                new GetRepositoryDetailsUseCase(repositoryCatalogPort)
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .addFilters(new CorrelationIdFilter())
                .build();
    }

    @Test
    void searchesRepositories() throws Exception {
        mockMvc.perform(get("/api/repositories").param("query", "app"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectKey").value("PRJ"))
                .andExpect(jsonPath("$[0].slug").value("app"));
    }

    @Test
    void returnsBadRequestForBlankSearch() throws Exception {
        mockMvc.perform(get("/api/repositories").param("query", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void returnsRepositoryDetails() throws Exception {
        mockMvc.perform(get("/api/repositories/PRJ/app"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectKey").value("PRJ"))
                .andExpect(jsonPath("$.versions[0].environment").value("REC"))
                .andExpect(jsonPath("$.versions[0].version").value("1.2.3"));
    }

    @Test
    void returnsNotFoundWhenRepositoryDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/repositories/PRJ/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("REPOSITORY_NOT_FOUND"));
    }

    @Test
    void propagatesCorrelationIdHeader() throws Exception {
        mockMvc.perform(get("/api/repositories").param("query", "app").header(CorrelationIdFilter.HEADER_NAME, "test-correlation"))
                .andExpect(status().isOk())
                .andExpect(header().string(CorrelationIdFilter.HEADER_NAME, "test-correlation"));
    }

    private static class StubRepositoryCatalogPort implements RepositoryCatalogPort {

        @Override
        public List<ApplicationRepository> searchRepositories(String query, int limit) {
            return List.of(repository(List.of()));
        }

        @Override
        public Optional<ApplicationRepository> findRepository(RepositoryCoordinates repository) {
            if ("missing".equals(repository.slug())) {
                return Optional.empty();
            }
            return Optional.of(repository(List.of()));
        }

        @Override
        public List<RepositoryVersion> findVersions(RepositoryCoordinates repository) {
            return List.of(new RepositoryVersion(Environment.REC, "1.2.3", null));
        }

        private ApplicationRepository repository(List<RepositoryVersion> versions) {
            return new ApplicationRepository("PRJ", "app", "Application", "https://bitbucket/app", "", versions);
        }
    }
}
