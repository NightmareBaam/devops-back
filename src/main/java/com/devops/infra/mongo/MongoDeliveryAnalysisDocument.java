package com.devops.infra.mongo;

import com.devops.domain.model.ApplicationAnalysis;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "delivery_analyses")
public record MongoDeliveryAnalysisDocument(
        @Id String id,
        ApplicationAnalysis analysis,
        Instant createdAt
) {
}
