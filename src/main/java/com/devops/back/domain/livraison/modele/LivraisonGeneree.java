package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.fla.modele.FicheLivraison;

public record LivraisonGeneree(
        Livraison livraison,
        FicheLivraison ficheLivraison,
        RecapitulatifLivraison recapitulatif
) {
}
