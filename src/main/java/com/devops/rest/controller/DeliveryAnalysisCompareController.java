package com.devops.rest.controller;

import com.devops.rest.DeliveryAnalysisCompareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-analyses/compare")
public class DeliveryAnalysisCompareController {

    private final DeliveryAnalysisCompareService service;

    public DeliveryAnalysisCompareController(DeliveryAnalysisCompareService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<List<DeliveryAnalysisCompareService.ComparisonAnalysisResponse>> compare(
            @RequestBody DeliveryAnalysisCompareService.ComparisonAnalysisRequest request
    ) {
        return ResponseEntity.ok(service.compare(request));
    }
}
