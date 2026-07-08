package com.devops.back.domain.livraison.casusage;

import com.devops.back.domain.commun.casusage.ValidationCasUsage;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.devops.back.domain.livraison.modele.ApplicatifAnalyse;
import com.devops.back.domain.livraison.modele.CommitAnalyse;
import com.devops.back.domain.livraison.modele.DemandeAnalyseLivraison;
import com.devops.back.domain.commun.modele.ReferenceVersion;
import com.devops.back.domain.livraison.modele.ResumeCommits;
import com.devops.back.domain.port.PortBitbucket;

import java.util.LinkedHashSet;
import java.util.List;

public class AnalyserLivraison {

    private final PortBitbucket portBitbucket;
    private final ExtracteurClesJira extracteurClesJira;

    public AnalyserLivraison(PortBitbucket portBitbucket) {
        this(portBitbucket, new ExtracteurClesJira());
    }

    public AnalyserLivraison(PortBitbucket portBitbucket, ExtracteurClesJira extracteurClesJira) {
        this.portBitbucket = portBitbucket;
        this.extracteurClesJira = extracteurClesJira;
    }

    public List<AnalyseLivraison> executer(DemandeAnalyseLivraison demande) {
        ValidationCasUsage.objetObligatoire(demande, "demande");
        ValidationCasUsage.collectionObligatoire(demande.applicatifs(), "applicatifs");
        ValidationCasUsage.objetObligatoire(demande.cible(), "cible");

        return demande.applicatifs().stream()
                .map(applicatif -> analyserApplicatif(applicatif, demande.cible()))
                .toList();
    }

    private AnalyseLivraison analyserApplicatif(ApplicatifAnalyse applicatif, ReferenceVersion cible) {
        ValidationCasUsage.objetObligatoire(applicatif, "applicatif");
        ValidationCasUsage.texteObligatoire(applicatif.projet(), "projet");
        ValidationCasUsage.texteObligatoire(applicatif.slug(), "slug");
        ValidationCasUsage.objetObligatoire(applicatif.base(), "base");

        var analyse = portBitbucket.comparerApplicatif(applicatif, cible);
        return completerJiras(analyse);
    }

    private AnalyseLivraison completerJiras(AnalyseLivraison analyse) {
        ValidationCasUsage.objetObligatoire(analyse, "analyse");

        var commits = analyse.commits();
        if (commits == null || commits.details() == null) {
            return analyse;
        }

        var details = commits.details().stream()
                .map(this::completerJirasCommit)
                .toList();
        var jiras = new LinkedHashSet<String>();
        if (analyse.jiras() != null) {
            jiras.addAll(analyse.jiras());
        }
        details.forEach(commit -> jiras.addAll(commit.jiras()));

        return new AnalyseLivraison(
                analyse.projet(),
                analyse.slug(),
                analyse.statut(),
                new ResumeCommits(commits.nombre(), details),
                analyse.configuration(),
                analyse.nombreFichiersModifies(),
                List.copyOf(jiras)
        );
    }

    private CommitAnalyse completerJirasCommit(CommitAnalyse commit) {
        var jiras = new LinkedHashSet<String>();
        if (commit.jiras() != null) {
            jiras.addAll(commit.jiras());
        }
        jiras.addAll(extracteurClesJira.extraire(commit.libelle()));

        return new CommitAnalyse(
                commit.hash(),
                commit.libelle(),
                commit.auteur(),
                List.copyOf(jiras),
                commit.configurationModifiee()
        );
    }
}
