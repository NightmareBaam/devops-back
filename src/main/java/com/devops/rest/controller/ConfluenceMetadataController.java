package com.devops.rest.controller;

import com.devops.domain.model.ConfluenceSpace;
import com.devops.domain.model.ConfluenceTemplate;
import com.devops.domain.usecase.GetConfluenceMetadataUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/confluence")
public class ConfluenceMetadataController {

    private final GetConfluenceMetadataUseCase useCase;

    public ConfluenceMetadataController(GetConfluenceMetadataUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/spaces")
    public ResponseEntity<List<ConfluenceSpace>> spaces() {
        return ResponseEntity.ok(useCase.listSpaces());
    }

    @GetMapping("/templates")
    public ResponseEntity<List<ConfluenceTemplate>> templates(@RequestParam(required = false) String spaceKey) {
        return ResponseEntity.ok(useCase.listTemplates(spaceKey));
    }
}
