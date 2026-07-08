package com.devops.back.domain.groupe.casusage;

import com.devops.back.domain.commun.casusage.ValidationCasUsage;

import com.devops.back.domain.commun.modele.DetailRepositoryBitbucket;
import com.devops.back.domain.port.PortBitbucket;

public class RecupererDetailRepository {

    private final PortBitbucket portBitbucket;

    public RecupererDetailRepository(PortBitbucket portBitbucket) {
        this.portBitbucket = portBitbucket;
    }

    public DetailRepositoryBitbucket executer(String projet, String slug) {
        return portBitbucket.recupererDetailRepository(
                ValidationCasUsage.texteObligatoire(projet, "projet"),
                ValidationCasUsage.texteObligatoire(slug, "slug")
        );
    }
}
