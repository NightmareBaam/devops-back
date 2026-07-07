package com.devops.rest.controller;

import com.devops.domain.model.RepositoryGroup;
import com.devops.domain.model.RepositoryGroupCommand;
import com.devops.domain.usecase.ManageRepositoryGroupsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/repository-groups")
public class RepositoryGroupController {

    private final ManageRepositoryGroupsUseCase useCase;

    public RepositoryGroupController(ManageRepositoryGroupsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<List<RepositoryGroup>> findAll() {
        return ResponseEntity.ok(useCase.findAll());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<RepositoryGroup> findById(@PathVariable String groupId) {
        return ResponseEntity.ok(useCase.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository group not found")));
    }

    @PostMapping
    public ResponseEntity<RepositoryGroup> create(
            @RequestBody RepositoryGroupCommand request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.create(request));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<RepositoryGroup> update(
            @PathVariable String groupId,
            @RequestBody RepositoryGroupCommand request
    ) {
        return ResponseEntity.ok(useCase.update(groupId, request));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(@PathVariable String groupId) {
        useCase.delete(groupId);
        return ResponseEntity.noContent().build();
    }
}
