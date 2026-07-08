package com.devops.back.domain.livraison.modele;

import com.devops.back.domain.commun.modele.Listes;

import java.util.List;

public record ResumeCommits(
        int nombre,
        List<CommitAnalyse> details
) {

    public ResumeCommits {
        details = Listes.copie(details);
    }
}
