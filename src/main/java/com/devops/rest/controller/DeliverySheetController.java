package com.devops.rest.controller;

import com.devops.domain.model.DeliverySheet;
import com.devops.domain.model.DeliverySheetCreationCommand;
import com.devops.domain.model.DeliverySheetCreationResult;
import com.devops.domain.model.DeliverySheetPage;
import com.devops.domain.model.Environment;
import com.devops.domain.usecase.ManageDeliverySheetsUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/delivery-sheets")
public class DeliverySheetController {

    private final ManageDeliverySheetsUseCase useCase;

    public DeliverySheetController(ManageDeliverySheetsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<DeliverySheetPage> findAll(
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) Environment environment,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(useCase.findAll(groupId, environment, status, from, to, query, page, pageSize));
    }

    @GetMapping("/{deliverySheetId}")
    public ResponseEntity<DeliverySheet> findById(@PathVariable String deliverySheetId) {
        return ResponseEntity.ok(useCase.findById(deliverySheetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery sheet not found")));
    }

    @PostMapping
    public ResponseEntity<DeliverySheetCreationResult> create(
            @RequestBody DeliverySheetCreationCommand request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(request));
    }

    @PostMapping("/{deliverySheetId}/duplicate")
    public ResponseEntity<DeliverySheet> duplicate(@PathVariable String deliverySheetId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.duplicate(deliverySheetId));
    }
}
