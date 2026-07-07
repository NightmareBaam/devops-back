package com.devops.infra.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDeliverySheetRepository extends MongoRepository<MongoDeliverySheetDocument, String> {
}
