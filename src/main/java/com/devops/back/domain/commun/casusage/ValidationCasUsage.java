package com.devops.back.domain.commun.casusage;

import java.util.Collection;
import java.util.UUID;

public final class ValidationCasUsage {

    private ValidationCasUsage() {
    }

    public static String texteObligatoire(String valeur, String nomChamp) {
        if (valeur == null || valeur.isBlank()) {
            throw new ExceptionMetier("Le champ " + nomChamp + " est obligatoire.");
        }
        return valeur;
    }

    public static <T> Collection<T> collectionObligatoire(Collection<T> valeur, String nomChamp) {
        if (valeur == null || valeur.isEmpty()) {
            throw new ExceptionMetier("Le champ " + nomChamp + " est obligatoire.");
        }
        return valeur;
    }

    public static <T> T objetObligatoire(T valeur, String nomChamp) {
        if (valeur == null) {
            throw new ExceptionMetier("Le champ " + nomChamp + " est obligatoire.");
        }
        return valeur;
    }

    public static UUID identifiantObligatoire(UUID valeur, String nomChamp) {
        if (valeur == null) {
            throw new ExceptionMetier("Le champ " + nomChamp + " est obligatoire.");
        }
        return valeur;
    }

    public static void paginationValide(int start, int limit) {
        if (start < 0) {
            throw new ExceptionMetier("Le parametre start doit etre positif ou nul.");
        }
        if (limit <= 0) {
            throw new ExceptionMetier("Le parametre limit doit etre strictement positif.");
        }
    }
}
