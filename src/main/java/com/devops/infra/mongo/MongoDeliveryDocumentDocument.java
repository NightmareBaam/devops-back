package com.devops.infra.mongo;

import com.devops.domain.model.DeliveryDocument;
import com.devops.domain.model.DeliverySummary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "delivery_documents")
public record MongoDeliveryDocumentDocument(
        @Id String id,
        DeliverySummary summary,
        DeliveryDocument document,
        Instant createdAt
) {
}
