package com.devops.infra.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDeliveryDocumentRepository extends MongoRepository<MongoDeliveryDocumentDocument, String> {
}
