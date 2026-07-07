package com.devops.domain.port;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.DeliveryAnalysisRecord;
import com.devops.domain.model.DeliveryDocument;
import com.devops.domain.model.DeliveryDocumentRecord;
import com.devops.domain.model.DeliverySummary;

import java.util.List;
import java.util.Optional;

public interface DeliveryRecordPort {

    DeliveryAnalysisRecord saveAnalysis(ApplicationAnalysis analysis);

    Optional<DeliveryAnalysisRecord> findAnalysisById(String analysisId);

    List<DeliveryAnalysisRecord> findRecentAnalyses(int limit);

    DeliveryDocumentRecord saveDocument(DeliverySummary summary, DeliveryDocument document);

    Optional<DeliveryDocumentRecord> findDocumentById(String documentId);
}
