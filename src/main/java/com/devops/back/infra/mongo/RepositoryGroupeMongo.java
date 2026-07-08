package com.devops.back.infra.mongo;

import com.devops.back.infra.mongo.model.GroupeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RepositoryGroupeMongo extends MongoRepository<GroupeDocument, UUID> {
}
