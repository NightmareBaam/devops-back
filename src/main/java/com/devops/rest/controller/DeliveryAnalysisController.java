package com.devops.rest.controller;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.DeliveryAnalysisRequest;
import com.devops.domain.usecase.AnalyzeApplicationsUseCase;
import com.devops.rest.ApplicationAnalysisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-analyses")
public class DeliveryAnalysisController {

    private final AnalyzeApplicationsUseCase analyzeApplicationsUseCase;

    public DeliveryAnalysisController(AnalyzeApplicationsUseCase analyzeApplicationsUseCase) {
        this.analyzeApplicationsUseCase = analyzeApplicationsUseCase;
    }

    @PostMapping
    public ResponseEntity<List<ApplicationAnalysisResponse>> analyzeApplications(@RequestBody DeliveryAnalysisRequest request) {
        List<ApplicationAnalysis> analyses = analyzeApplicationsUseCase.analyze(request);
        return ResponseEntity.ok(analyses.stream()
                .map(ApplicationAnalysisResponse::from)
                .toList());
    }
}
