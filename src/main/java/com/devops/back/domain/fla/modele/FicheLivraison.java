package com.devops.back.domain.fla.modele;

import java.time.LocalDateTime;
import java.util.UUID;

public record FicheLivraison(
        String libelle,
        String url,
        LocalDateTime date,
        String auteur,
        UUID groupe,
        UUID livraison
) {
}
