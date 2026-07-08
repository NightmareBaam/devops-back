package com.devops.back.infra.mongo;

import com.devops.back.infra.mongo.model.FicheLivraisonDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RepositoryFicheLivraisonMongo extends MongoRepository<FicheLivraisonDocument, UUID> {
}
