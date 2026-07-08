package com.devops.back.domain.livraison.modele;

public record ModificationConfiguration(
        String cle,
        Object ancienneValeur,
        Object nouvelleValeur
) {
}
