package com.devops.back.domain.port;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.devops.back.domain.livraison.modele.ApplicatifAnalyse;
import com.devops.back.domain.commun.modele.DetailRepositoryBitbucket;
import com.devops.back.domain.commun.modele.ReferenceVersion;
import com.devops.back.domain.commun.modele.RepositoryBitbucket;

import java.util.List;

public interface PortBitbucket {

    List<RepositoryBitbucket> rechercherRepositories(String nom);

    DetailRepositoryBitbucket recupererDetailRepository(String projet, String slug);

    List<String> recupererBranches(String projet, String slug);

    List<String> recupererTags(String projet, String slug);

    AnalyseLivraison comparerApplicatif(ApplicatifAnalyse applicatif, ReferenceVersion cible);
}
