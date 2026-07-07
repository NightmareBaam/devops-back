package com.devops.infra.mongo;

import com.devops.domain.model.Environment;
import com.devops.domain.model.OrderedRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "repository_groups")
public record MongoRepositoryGroupDocument(
        @Id String id,
        String name,
        String description,
        List<OrderedRepository> repositories,
        Environment defaultEnvironment,
        String defaultTag,
        Instant updatedAt
) {
}
