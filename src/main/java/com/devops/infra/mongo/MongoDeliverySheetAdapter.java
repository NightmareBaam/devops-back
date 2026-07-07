package com.devops.infra.mongo;

import com.devops.domain.model.DeliverySheet;
import com.devops.domain.port.DeliverySheetPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnBean(MongoDeliverySheetRepository.class)
public class MongoDeliverySheetAdapter implements DeliverySheetPort {

    private final MongoDeliverySheetRepository repository;

    public MongoDeliverySheetAdapter(MongoDeliverySheetRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DeliverySheet> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<DeliverySheet> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public DeliverySheet save(DeliverySheet sheet) {
        return toDomain(repository.save(toDocument(sheet)));
    }

    private DeliverySheet toDomain(MongoDeliverySheetDocument document) {
        return new DeliverySheet(
                document.id(),
                document.title(),
                document.groupId(),
                document.groupName(),
                document.targetEnvironment(),
                document.status(),
                document.author(),
                document.deliveryDate(),
                document.createdAt(),
                document.confluenceUrl(),
                document.description(),
                document.jiraEpicKey(),
                document.selectedApplications(),
                document.jiraIssueKeys()
        );
    }

    private MongoDeliverySheetDocument toDocument(DeliverySheet sheet) {
        return new MongoDeliverySheetDocument(
                sheet.id(),
                sheet.title(),
                sheet.groupId(),
                sheet.groupName(),
                sheet.targetEnvironment(),
                sheet.status(),
                sheet.author(),
                sheet.deliveryDate(),
                sheet.createdAt(),
                sheet.confluenceUrl(),
                sheet.description(),
                sheet.jiraEpicKey(),
                sheet.selectedApplications(),
                sheet.jiraIssueKeys()
        );
    }
}
