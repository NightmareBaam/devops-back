package com.devops.infra.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoRepositoryGroupRepository extends MongoRepository<MongoRepositoryGroupDocument, String> {
}
