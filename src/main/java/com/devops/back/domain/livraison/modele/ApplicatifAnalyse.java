package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.ReferenceVersion;

public record ApplicatifAnalyse(
        String projet,
        String slug,
        ReferenceVersion base
) {
}
