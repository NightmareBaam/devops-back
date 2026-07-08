package com.devops.back.domain.groupe.casusage;

import com.devops.back.domain.commun.casusage.ValidationCasUsage;

import com.devops.back.domain.commun.modele.RepositoryBitbucket;
import com.devops.back.domain.port.PortBitbucket;

import java.util.List;

public class RechercherRepositories {

    private final PortBitbucket portBitbucket;

    public RechercherRepositories(PortBitbucket portBitbucket) {
        this.portBitbucket = portBitbucket;
    }

    public List<RepositoryBitbucket> executer(String nom) {
        return portBitbucket.rechercherRepositories(
                ValidationCasUsage.texteObligatoire(nom, "nom")
        );
    }
}
