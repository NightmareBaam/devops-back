package com.devops.infra.mongo;

import com.devops.domain.model.Environment;
import com.devops.domain.model.RepositoryRef;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "delivery_sheets")
public record MongoDeliverySheetDocument(
        @Id String id,
        String title,
        String groupId,
        String groupName,
        Environment targetEnvironment,
        String status,
        String author,
        LocalDate deliveryDate,
        Instant createdAt,
        String confluenceUrl,
        String description,
        String jiraEpicKey,
        List<RepositoryRef> selectedApplications,
        List<String> jiraIssueKeys
) {
}
