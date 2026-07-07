package com.devops.domain.port;

import com.devops.domain.model.RepositoryGroup;

import java.util.List;
import java.util.Optional;

public interface RepositoryGroupPort {

    List<RepositoryGroup> findAll();

    Optional<RepositoryGroup> findById(String id);

    RepositoryGroup save(RepositoryGroup group);

    void delete(String id);
}
