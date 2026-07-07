package com.devops.infra.mongo;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.DeliveryAnalysisRecord;
import com.devops.domain.model.DeliveryDocument;
import com.devops.domain.model.DeliveryDocumentRecord;
import com.devops.domain.model.DeliverySummary;
import com.devops.domain.port.DeliveryRecordPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnBean({MongoDeliveryAnalysisRepository.class, MongoDeliveryDocumentRepository.class})
public class MongoDeliveryRecordAdapter implements DeliveryRecordPort {

    private final MongoDeliveryAnalysisRepository analysisRepository;
    private final MongoDeliveryDocumentRepository documentRepository;

    public MongoDeliveryRecordAdapter(
            MongoDeliveryAnalysisRepository analysisRepository,
            MongoDeliveryDocumentRepository documentRepository
    ) {
        this.analysisRepository = analysisRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public DeliveryAnalysisRecord saveAnalysis(ApplicationAnalysis analysis) {
        MongoDeliveryAnalysisDocument saved = analysisRepository.save(new MongoDeliveryAnalysisDocument(
                UUID.randomUUID().toString(),
                analysis,
                Instant.now()
        ));
        return toRecord(saved);
    }

    @Override
    public Optional<DeliveryAnalysisRecord> findAnalysisById(String analysisId) {
        return analysisRepository.findById(analysisId).map(this::toRecord);
    }

    @Override
    public List<DeliveryAnalysisRecord> findRecentAnalyses(int limit) {
        int effectiveLimit = Math.max(1, limit);
        return analysisRepository.findAll(PageRequest.of(0, effectiveLimit, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(this::toRecord)
                .toList();
    }

    @Override
    public DeliveryDocumentRecord saveDocument(DeliverySummary summary, DeliveryDocument document) {
        MongoDeliveryDocumentDocument saved = documentRepository.save(new MongoDeliveryDocumentDocument(
                UUID.randomUUID().toString(),
                summary,
                document,
                Instant.now()
        ));
        return toRecord(saved);
    }

    @Override
    public Optional<DeliveryDocumentRecord> findDocumentById(String documentId) {
        return documentRepository.findById(documentId).map(this::toRecord);
    }

    private DeliveryAnalysisRecord toRecord(MongoDeliveryAnalysisDocument document) {
        return new DeliveryAnalysisRecord(document.id(), document.analysis(), document.createdAt());
    }

    private DeliveryDocumentRecord toRecord(MongoDeliveryDocumentDocument document) {
        return new DeliveryDocumentRecord(document.id(), document.summary(), document.document(), document.createdAt());
    }
}
