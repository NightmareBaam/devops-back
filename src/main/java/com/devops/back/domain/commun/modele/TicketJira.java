package com.devops.back.domain.commun.modele;

import java.util.List;

public record TicketJira(
        String cle,
        String libelle,
        String statut,
        String responsable,
        List<String> etiquettes
) {

    public TicketJira {
        etiquettes = Listes.copie(etiquettes);
    }
}
