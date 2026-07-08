package com.devops.back.infra.mongo.model;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.devops.back.domain.livraison.modele.ApplicatifAnalyse;
import com.devops.back.domain.commun.modele.ReferenceVersion;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document("livraisons")
public record LivraisonDocument(
        @Id UUID identifiant,
        String libelle,
        LocalDate dateLivraison,
        String description,
        ReferenceVersion cible,
        List<ApplicatifAnalyse> applicatifs,
        List<AnalyseLivraison> analyses,
        LocalDateTime dateCreation
) {
}
