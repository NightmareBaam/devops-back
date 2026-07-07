package com.devops.rest.controller;

import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.usecase.GetRepositoryDetailsUseCase;
import com.devops.domain.usecase.SearchRepositoriesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    private final SearchRepositoriesUseCase searchRepositoriesUseCase;
    private final GetRepositoryDetailsUseCase getRepositoryDetailsUseCase;

    public RepositoryController(
            SearchRepositoriesUseCase searchRepositoriesUseCase,
            GetRepositoryDetailsUseCase getRepositoryDetailsUseCase
    ) {
        this.searchRepositoriesUseCase = searchRepositoriesUseCase;
        this.getRepositoryDetailsUseCase = getRepositoryDetailsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ApplicationRepository>> searchRepositories(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", required = false, defaultValue = "25") int limit
    ) {
        return ResponseEntity.ok(searchRepositoriesUseCase.search(query, limit));
    }

    @GetMapping("/{projectKey}/{repositorySlug}")
    public ResponseEntity<ApplicationRepository> getRepositoryDetails(
            @PathVariable String projectKey,
            @PathVariable String repositorySlug
    ) {
        return ResponseEntity.ok(getRepositoryDetailsUseCase.getDetails(new RepositoryCoordinates(projectKey, repositorySlug)));
    }
}
