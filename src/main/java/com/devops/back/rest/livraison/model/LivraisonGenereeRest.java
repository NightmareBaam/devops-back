package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.LivraisonGeneree;

import java.util.UUID;

public record LivraisonGenereeRest(
        UUID livraison,
        String ficheLivraison,
        RecapitulatifLivraisonRest recapitulatif
) {

    public static LivraisonGenereeRest depuisDomaine(LivraisonGeneree livraisonGeneree) {
        return new LivraisonGenereeRest(
                livraisonGeneree.livraison().identifiant(),
                livraisonGeneree.ficheLivraison().url(),
                RecapitulatifLivraisonRest.depuisDomaine(livraisonGeneree.recapitulatif())
        );
    }
}
