package com.devops.back.rest.livraison.model;

import com.devops.back.domain.livraison.modele.ResumeCommits;

import java.util.List;

public record ResumeCommitsRest(
        int nombre,
        List<CommitAnalyseRest> details
) {

    public static ResumeCommitsRest depuisDomaine(ResumeCommits resumeCommits) {
        if (resumeCommits == null) {
            return new ResumeCommitsRest(0, List.of());
        }
        return new ResumeCommitsRest(
                resumeCommits.nombre(),
                resumeCommits.details().stream().map(CommitAnalyseRest::depuisDomaine).toList()
        );
    }
}
