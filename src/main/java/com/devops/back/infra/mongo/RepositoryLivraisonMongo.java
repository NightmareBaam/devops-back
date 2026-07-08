package com.devops.back.infra.mongo;

import com.devops.back.infra.mongo.model.LivraisonDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RepositoryLivraisonMongo extends MongoRepository<LivraisonDocument, UUID> {
}
