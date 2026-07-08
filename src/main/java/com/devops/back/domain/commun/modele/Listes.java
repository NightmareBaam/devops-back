package com.devops.back.domain.commun.modele;

import java.util.List;

public final class Listes {

    private Listes() {
    }

    public static <T> List<T> copie(List<T> valeurs) {
        if (valeurs == null) {
            return List.of();
        }
        return List.copyOf(valeurs);
    }
}
