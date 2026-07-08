package com.devops.back.domain.port;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.devops.back.domain.livraison.modele.DemandeGenerationLivraison;
import com.devops.back.domain.fla.modele.FicheLivraison;

import java.util.List;

public interface PortConfluence {

    FicheLivraison creerFicheLivraison(
            DemandeGenerationLivraison demande,
            List<AnalyseLivraison> analyses,
            String auteur
    );
}
