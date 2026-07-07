package com.devops.infra.bitbucket;

import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.model.SourceFileChange;
import com.devops.domain.model.SourceFileChangeType;
import com.devops.domain.model.SourceReference;
import com.devops.domain.model.SourceReferenceType;
import com.devops.domain.port.RepositoryCatalogPort;
import com.devops.domain.port.SourceControlPort;
import com.devops.infra.config.BasicAuthRestClientFactory;
import com.devops.infra.config.IntegrationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.yaml.snakeyaml.Yaml;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class BitbucketServerAdapter implements RepositoryCatalogPort, SourceControlPort {

    private static final int DEFAULT_PAGE_LIMIT = 200;
    private static final String APPLICATION_YML = "application.yml";

    private final RestClient restClient;

    public BitbucketServerAdapter(BasicAuthRestClientFactory restClientFactory, IntegrationProperties properties) {
        this.restClient = restClientFactory.create(properties.bitbucket());
    }

    @Override
    public List<ApplicationRepository> searchRepositories(String query, int limit) {
        BitbucketPage<BitbucketRepositoryResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/api/1.0/repos")
                        .queryParam("name", query)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return pageValues(response).stream()
                .map(this::toApplicationRepository)
                .toList();
    }

    @Override
    public Optional<ApplicationRepository> findRepository(RepositoryCoordinates repository) {
        BitbucketRepositoryResponse response = restClient.get()
                .uri("/rest/api/1.0/projects/{projectKey}/repos/{slug}", repository.projectKey(), repository.slug())
                .retrieve()
                .body(BitbucketRepositoryResponse.class);

        return Optional.ofNullable(response).map(this::toApplicationRepository);
    }

    @Override
    public List<RepositoryVersion> findVersions(RepositoryCoordinates repository) {
        return List.of();
    }

    @Override
    public List<CommitInfo> listCommitsBetween(
            RepositoryCoordinates repository,
            ComparisonReference fromReference,
            ComparisonReference toReference
    ) {
        BitbucketPage<BitbucketCommitResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/api/1.0/projects/{projectKey}/repos/{slug}/commits")
                        .queryParam("since", fromReference.label())
                        .queryParam("until", toReference.label())
                        .queryParam("limit", DEFAULT_PAGE_LIMIT)
                        .build(repository.projectKey(), repository.slug()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return pageValues(response).stream()
                .map(this::toCommitInfo)
                .toList();
    }

    @Override
    public Optional<ApplicationConfigurationFile> readApplicationConfiguration(
            RepositoryCoordinates repository,
            ComparisonReference reference
    ) {
        String content = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/api/1.0/projects/{projectKey}/repos/{slug}/raw/{filePath}")
                        .queryParam("at", reference.label())
                        .build(repository.projectKey(), repository.slug(), APPLICATION_YML))
                .retrieve()
                .body(String.class);

        if (content == null) {
            return Optional.empty();
        }

        return Optional.of(new ApplicationConfigurationFile(
                repository,
                reference,
                APPLICATION_YML,
                content,
                flattenYaml(content)
        ));
    }

    @Override
    public List<SourceFileChange> listChangedFiles(RepositoryCoordinates repository, String commitHash) {
        BitbucketPage<BitbucketChangeResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/api/1.0/projects/{projectKey}/repos/{slug}/commits/{commitHash}/changes")
                        .queryParam("limit", DEFAULT_PAGE_LIMIT)
                        .build(repository.projectKey(), repository.slug(), commitHash))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return pageValues(response).stream()
                .map(change -> new SourceFileChange(change.path().value(), toSourceFileChangeType(change.type())))
                .toList();
    }

    @Override
    public List<SourceReference> listBranches(RepositoryCoordinates repository) {
        return listReferences(repository, "/rest/api/1.0/projects/{projectKey}/repos/{slug}/branches", SourceReferenceType.BRANCH);
    }

    @Override
    public List<SourceReference> listTags(RepositoryCoordinates repository) {
        return listReferences(repository, "/rest/api/1.0/projects/{projectKey}/repos/{slug}/tags", SourceReferenceType.TAG);
    }

    private List<SourceReference> listReferences(RepositoryCoordinates repository, String path, SourceReferenceType type) {
        BitbucketPage<BitbucketReferenceResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("limit", DEFAULT_PAGE_LIMIT)
                        .build(repository.projectKey(), repository.slug()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return pageValues(response).stream()
                .map(reference -> new SourceReference(reference.displayId(), type, reference.latestCommit()))
                .toList();
    }

    private ApplicationRepository toApplicationRepository(BitbucketRepositoryResponse repository) {
        String projectKey = repository.project() == null ? "" : repository.project().key();
        return new ApplicationRepository(
                projectKey,
                repository.slug(),
                repository.name(),
                repository.url(),
                repository.description(),
                List.of()
        );
    }

    private CommitInfo toCommitInfo(BitbucketCommitResponse commit) {
        return new CommitInfo(
                firstNonBlank(commit.id(), commit.displayId()),
                commit.message(),
                commit.authorName(),
                commit.authorTimestamp() == null ? null : Instant.ofEpochMilli(commit.authorTimestamp()),
                java.util.Set.of(),
                false
        );
    }

    private SourceFileChangeType toSourceFileChangeType(String type) {
        if (type == null) {
            return SourceFileChangeType.MODIFIED;
        }
        return switch (type.toUpperCase()) {
            case "ADD" -> SourceFileChangeType.ADDED;
            case "DELETE" -> SourceFileChangeType.DELETED;
            case "MOVE" -> SourceFileChangeType.RENAMED;
            default -> SourceFileChangeType.MODIFIED;
        };
    }

    private Map<String, String> flattenYaml(String content) {
        Object loaded = new Yaml().load(content);
        Map<String, String> flattened = new LinkedHashMap<>();
        flatten("", loaded, flattened);
        return flattened;
    }

    @SuppressWarnings("unchecked")
    private void flatten(String prefix, Object value, Map<String, String> flattened) {
        if (value instanceof Map<?, ?> map) {
            map.forEach((key, childValue) -> {
                String childKey = prefix.isBlank() ? String.valueOf(key) : prefix + "." + key;
                flatten(childKey, childValue, flattened);
            });
        } else if (value instanceof List<?> list) {
            for (int index = 0; index < list.size(); index++) {
                flatten(prefix + "[" + index + "]", list.get(index), flattened);
            }
        } else if (!prefix.isBlank()) {
            flattened.put(prefix, value == null ? "" : String.valueOf(value));
        }
    }

    private <T> List<T> pageValues(BitbucketPage<T> response) {
        return response == null || response.values() == null ? List.of() : response.values();
    }

    private String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : second;
    }
}
