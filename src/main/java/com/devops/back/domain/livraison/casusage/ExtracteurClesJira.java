package com.devops.back.domain.livraison.casusage;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ExtracteurClesJira {

    private static final Pattern CLE_JIRA = Pattern.compile("\\b[A-Z]+-[0-9]+\\b");

    public List<String> extraire(String texte) {
        if (texte == null || texte.isBlank()) {
            return List.of();
        }
        return CLE_JIRA.matcher(texte)
                .results()
                .map(MatchResult::group)
                .distinct()
                .toList();
    }
}
