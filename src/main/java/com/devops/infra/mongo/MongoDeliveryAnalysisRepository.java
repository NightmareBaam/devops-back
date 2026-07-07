package com.devops.infra.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDeliveryAnalysisRepository extends MongoRepository<MongoDeliveryAnalysisDocument, String> {
}
