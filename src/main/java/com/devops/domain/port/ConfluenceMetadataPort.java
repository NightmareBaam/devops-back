package com.devops.domain.port;

import com.devops.domain.model.ConfluenceSpace;
import com.devops.domain.model.ConfluenceTemplate;

import java.util.List;

public interface ConfluenceMetadataPort {

    List<ConfluenceSpace> listSpaces();

    List<ConfluenceTemplate> listTemplates(String spaceKey);
}
