package com.devops.back.infra.mongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("fiches_livraison")
public record FicheLivraisonDocument(
        @Id UUID identifiant,
        String libelle,
        String url,
        LocalDateTime date,
        String auteur,
        UUID groupe,
        UUID livraison
) {
}
