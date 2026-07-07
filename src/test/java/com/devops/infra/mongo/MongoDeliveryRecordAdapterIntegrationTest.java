package com.devops.infra.mongo;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.DeliveryAnalysisRecord;
import com.devops.domain.model.DeliveryDocument;
import com.devops.domain.model.DeliveryDocumentRecord;
import com.devops.domain.model.DeliveryDocumentStatus;
import com.devops.domain.model.DeliverySummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class MongoDeliveryRecordAdapterIntegrationTest {

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:7.0");

    @Autowired
    private MongoDeliveryRecordAdapter adapter;

    @Autowired
    private MongoDeliveryAnalysisRepository analysisRepository;

    @Autowired
    private MongoDeliveryDocumentRepository documentRepository;

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }

    @AfterEach
    void cleanDatabase() {
        analysisRepository.deleteAll();
        documentRepository.deleteAll();
    }

    @Test
    void savesAndReadsDeliveryAnalysis() {
        ApplicationAnalysis analysis = analysis();

        DeliveryAnalysisRecord saved = adapter.saveAnalysis(analysis);
        DeliveryAnalysisRecord found = adapter.findAnalysisById(saved.id()).orElseThrow();

        assertThat(found.analysis().repository().slug()).isEqualTo("app");
        assertThat(adapter.findRecentAnalyses(10)).hasSize(1);
    }

    @Test
    void savesAndReadsDeliveryDocument() {
        DeliverySummary summary = new DeliverySummary(List.of(analysis()), Set.of("ABC-123"), 1, false, Instant.parse("2026-07-02T12:00:00Z"));
        DeliveryDocument document = new DeliveryDocument("conf-1", "FLA", "FLA", "http://confluence/page", "<h1>FLA</h1>", DeliveryDocumentStatus.CREATED, Instant.now());

        DeliveryDocumentRecord saved = adapter.saveDocument(summary, document);
        DeliveryDocumentRecord found = adapter.findDocumentById(saved.id()).orElseThrow();

        assertThat(found.document().id()).isEqualTo("conf-1");
        assertThat(found.summary().totalCommitCount()).isEqualTo(1);
    }

    private ApplicationAnalysis analysis() {
        return new ApplicationAnalysis(
                new ApplicationRepository("PRJ", "app", "Application", "https://bitbucket/app", "", List.of()),
                ComparisonReference.branch("develop"),
                List.of(new CommitInfo("abc", "ABC-123 commit", "alice", Instant.parse("2026-07-02T00:00:00Z"), Set.of("ABC-123"), false)),
                List.of(),
                List.of(),
                List.of()
        );
    }
}
