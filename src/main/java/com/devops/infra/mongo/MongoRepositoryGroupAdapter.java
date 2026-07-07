package com.devops.infra.mongo;

import com.devops.domain.model.RepositoryGroup;
import com.devops.domain.port.RepositoryGroupPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnBean(MongoRepositoryGroupRepository.class)
public class MongoRepositoryGroupAdapter implements RepositoryGroupPort {

    private final MongoRepositoryGroupRepository repository;

    public MongoRepositoryGroupAdapter(MongoRepositoryGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RepositoryGroup> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<RepositoryGroup> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public RepositoryGroup save(RepositoryGroup group) {
        return toDomain(repository.save(toDocument(group)));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    private RepositoryGroup toDomain(MongoRepositoryGroupDocument document) {
        return new RepositoryGroup(
                document.id(),
                document.name(),
                document.description(),
                document.repositories(),
                document.defaultEnvironment(),
                document.defaultTag(),
                document.updatedAt()
        );
    }

    private MongoRepositoryGroupDocument toDocument(RepositoryGroup group) {
        return new MongoRepositoryGroupDocument(
                group.id(),
                group.name(),
                group.description(),
                group.repositories(),
                group.defaultEnvironment(),
                group.defaultTag(),
                group.updatedAt()
        );
    }
}
