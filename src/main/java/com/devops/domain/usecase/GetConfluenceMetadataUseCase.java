package com.devops.domain.usecase;

import com.devops.domain.model.ConfluenceSpace;
import com.devops.domain.model.ConfluenceTemplate;
import com.devops.domain.port.ConfluenceMetadataPort;

import java.util.List;

public class GetConfluenceMetadataUseCase {

    private final ConfluenceMetadataPort confluenceMetadataPort;

    public GetConfluenceMetadataUseCase(ConfluenceMetadataPort confluenceMetadataPort) {
        this.confluenceMetadataPort = confluenceMetadataPort;
    }

    public List<ConfluenceSpace> listSpaces() {
        return confluenceMetadataPort.listSpaces();
    }

    public List<ConfluenceTemplate> listTemplates(String spaceKey) {
        return confluenceMetadataPort.listTemplates(spaceKey);
    }
}
