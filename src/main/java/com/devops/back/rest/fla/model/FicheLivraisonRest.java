package com.devops.back.rest.fla.model;

import com.devops.back.domain.fla.modele.FicheLivraison;

import java.time.LocalDateTime;
import java.util.UUID;

public record FicheLivraisonRest(
        String libelle,
        String url,
        LocalDateTime date,
        String auteur,
        UUID groupe,
        UUID livraison
) {

    public static FicheLivraisonRest depuisDomaine(FicheLivraison ficheLivraison) {
        return new FicheLivraisonRest(
                ficheLivraison.libelle(),
                ficheLivraison.url(),
                ficheLivraison.date(),
                ficheLivraison.auteur(),
                ficheLivraison.groupe(),
                ficheLivraison.livraison()
        );
    }
}
