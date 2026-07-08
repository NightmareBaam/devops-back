package com.devops.back.domain.fla.casusage;

import com.devops.back.domain.commun.casusage.ValidationCasUsage;
import com.devops.back.domain.fla.modele.FicheLivraison;
import com.devops.back.domain.port.PortMongo;

import java.util.List;

public class RechercherFichesLivraison {

    private final PortMongo portMongo;

    public RechercherFichesLivraison(PortMongo portMongo) {
        this.portMongo = portMongo;
    }

    public List<FicheLivraison> executer(int start, int limit) {
        ValidationCasUsage.paginationValide(start, limit);
        return portMongo.rechercherFichesLivraison(start, limit);
    }
}
