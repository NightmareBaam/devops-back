package com.devops.domain.usecase;

import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.OrderedRepository;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryGroup;
import com.devops.domain.model.RepositoryGroupCommand;
import com.devops.domain.port.RepositoryCatalogPort;
import com.devops.domain.port.RepositoryGroupPort;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManageRepositoryGroupsUseCase {

    private final RepositoryGroupPort repositoryGroupPort;
    private final RepositoryCatalogPort repositoryCatalogPort;

    public ManageRepositoryGroupsUseCase(
            RepositoryGroupPort repositoryGroupPort,
            RepositoryCatalogPort repositoryCatalogPort
    ) {
        this.repositoryGroupPort = repositoryGroupPort;
        this.repositoryCatalogPort = repositoryCatalogPort;
    }

    public List<RepositoryGroup> findAll() {
        return repositoryGroupPort.findAll().stream()
                .sorted(Comparator.comparing(RepositoryGroup::name))
                .toList();
    }

    public Optional<RepositoryGroup> findById(String id) {
        return repositoryGroupPort.findById(id);
    }

    public RepositoryGroup create(RepositoryGroupCommand command) {
        return save("grp-" + UUID.randomUUID(), command);
    }

    public RepositoryGroup update(String id, RepositoryGroupCommand command) {
        return save(id, command);
    }

    public void delete(String id) {
        repositoryGroupPort.delete(id);
    }

    private RepositoryGroup save(String id, RepositoryGroupCommand command) {
        RepositoryGroup group = new RepositoryGroup(
                id,
                command.name(),
                command.description(),
                command.repositories().stream()
                        .map(repository -> new OrderedRepository(
                                repository.order(),
                                resolveRepository(repository.projectKey(), repository.slug())
                        ))
                        .toList(),
                command.defaultEnvironment(),
                command.defaultTag(),
                Instant.now()
        );
        return repositoryGroupPort.save(group);
    }

    private ApplicationRepository resolveRepository(String projectKey, String slug) {
        return repositoryCatalogPort.findRepository(new RepositoryCoordinates(projectKey, slug))
                .orElseGet(() -> new ApplicationRepository(projectKey, slug, slug, "", "", List.of()));
    }
}
