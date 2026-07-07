package com.devops.domain.usecase;

import com.devops.domain.model.DeliverySheet;
import com.devops.domain.model.DeliverySheetCreationCommand;
import com.devops.domain.model.DeliverySheetCreationResult;
import com.devops.domain.model.DeliverySheetPage;
import com.devops.domain.model.DeliverySheetSummary;
import com.devops.domain.model.Environment;
import com.devops.domain.port.DeliverySheetDocumentPort;
import com.devops.domain.port.DeliverySheetPort;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManageDeliverySheetsUseCase {

    private final DeliverySheetPort deliverySheetPort;
    private final DeliverySheetDocumentPort deliverySheetDocumentPort;

    public ManageDeliverySheetsUseCase(
            DeliverySheetPort deliverySheetPort,
            DeliverySheetDocumentPort deliverySheetDocumentPort
    ) {
        this.deliverySheetPort = deliverySheetPort;
        this.deliverySheetDocumentPort = deliverySheetDocumentPort;
    }

    public DeliverySheetPage findAll(
            String groupId,
            Environment environment,
            String status,
            Instant from,
            Instant to,
            String query,
            int page,
            int pageSize
    ) {
        int effectivePage = Math.max(1, page);
        int effectivePageSize = Math.max(1, Math.min(pageSize, 100));
        List<DeliverySheetSummary> allItems = deliverySheetPort.findAll().stream()
                .filter(sheet -> groupId == null || groupId.isBlank() || groupId.equals(sheet.groupId()))
                .filter(sheet -> environment == null || environment == sheet.targetEnvironment())
                .filter(sheet -> status == null || status.isBlank() || status.equalsIgnoreCase(sheet.status()))
                .filter(sheet -> from == null || !sheet.createdAt().isBefore(from))
                .filter(sheet -> to == null || !sheet.createdAt().isAfter(to))
                .filter(sheet -> matchesQuery(sheet, query))
                .sorted(Comparator.comparing(DeliverySheet::createdAt).reversed())
                .map(DeliverySheetSummary::from)
                .toList();
        int startIndex = Math.min((effectivePage - 1) * effectivePageSize, allItems.size());
        int endIndex = Math.min(startIndex + effectivePageSize, allItems.size());
        return new DeliverySheetPage(allItems.subList(startIndex, endIndex), effectivePage, effectivePageSize, allItems.size());
    }

    public Optional<DeliverySheet> findById(String id) {
        return deliverySheetPort.findById(id);
    }

    public DeliverySheetCreationResult create(DeliverySheetCreationCommand command) {
        String id = "FLA-" + Instant.now().getEpochSecond() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String confluenceUrl = deliverySheetDocumentPort.createPage(id, command);
        DeliverySheet sheet = new DeliverySheet(
                id,
                command.title(),
                command.groupId(),
                "",
                command.targetEnvironment(),
                "CREATED",
                command.responsible(),
                command.deliveryDate(),
                Instant.now(),
                confluenceUrl,
                command.description(),
                command.jiraEpicKey(),
                command.selectedApplications(),
                command.jiraIssueKeys()
        );
        deliverySheetPort.save(sheet);
        return new DeliverySheetCreationResult(id, confluenceUrl, command.jiraIssueKeys());
    }

    public DeliverySheet duplicate(String id) {
        DeliverySheet existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery sheet not found: " + id));
        DeliverySheet duplicated = new DeliverySheet(
                "FLA-" + Instant.now().getEpochSecond() + "-" + UUID.randomUUID().toString().substring(0, 8),
                existing.title() + " - copie",
                existing.groupId(),
                existing.groupName(),
                existing.targetEnvironment(),
                "DRAFT",
                existing.author(),
                existing.deliveryDate(),
                Instant.now(),
                existing.confluenceUrl(),
                existing.description(),
                existing.jiraEpicKey(),
                existing.selectedApplications(),
                existing.jiraIssueKeys()
        );
        return deliverySheetPort.save(duplicated);
    }

    private boolean matchesQuery(DeliverySheet sheet, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String normalizedQuery = query.toLowerCase();
        return contains(sheet.id(), normalizedQuery)
                || contains(sheet.title(), normalizedQuery)
                || contains(sheet.author(), normalizedQuery);
    }

    private boolean contains(String value, String normalizedQuery) {
        return value != null && value.toLowerCase().contains(normalizedQuery);
    }
}
