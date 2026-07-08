package com.devops.back.domain.port;

import com.devops.back.domain.fla.modele.FicheLivraison;
import com.devops.back.domain.groupe.modele.Groupe;
import com.devops.back.domain.livraison.modele.Livraison;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortMongo {

    List<Groupe> rechercherGroupes(int start, int limit);

    Optional<Groupe> recupererGroupe(UUID identifiant);

    Groupe sauvegarderGroupe(Groupe groupe);

    List<Livraison> rechercherLivraisons(int start, int limit);

    Optional<Livraison> recupererLivraison(UUID identifiant);

    Livraison sauvegarderLivraison(Livraison livraison);

    List<FicheLivraison> rechercherFichesLivraison(int start, int limit);

    FicheLivraison sauvegarderFicheLivraison(FicheLivraison ficheLivraison);
}
