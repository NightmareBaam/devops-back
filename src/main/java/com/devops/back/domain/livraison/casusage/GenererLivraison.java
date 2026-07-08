package com.devops.back.domain.livraison.casusage;

import com.devops.back.domain.commun.casusage.ValidationCasUsage;

import com.devops.back.domain.livraison.modele.DemandeAnalyseLivraison;
import com.devops.back.domain.livraison.modele.DemandeGenerationLivraison;
import com.devops.back.domain.livraison.modele.Livraison;
import com.devops.back.domain.livraison.modele.LivraisonGeneree;
import com.devops.back.domain.port.PortConfluence;
import com.devops.back.domain.port.PortJira;
import com.devops.back.domain.port.PortMongo;

import java.time.LocalDateTime;
import java.util.UUID;

public class GenererLivraison {

    private final AnalyserLivraison analyserLivraison;
    private final GenererRecapitulatifLivraison genererRecapitulatifLivraison;
    private final PortJira portJira;
    private final PortConfluence portConfluence;
    private final PortMongo portMongo;

    public GenererLivraison(
            AnalyserLivraison analyserLivraison,
            PortJira portJira,
            PortConfluence portConfluence,
            PortMongo portMongo
    ) {
        this.analyserLivraison = analyserLivraison;
        this.genererRecapitulatifLivraison = new GenererRecapitulatifLivraison();
        this.portJira = portJira;
        this.portConfluence = portConfluence;
        this.portMongo = portMongo;
    }

    public LivraisonGeneree executer(DemandeGenerationLivraison demande, String auteur, String etiquetteLivraison) {
        ValidationCasUsage.objetObligatoire(demande, "demande");
        ValidationCasUsage.texteObligatoire(demande.libelle(), "libelle");
        ValidationCasUsage.objetObligatoire(demande.dateLivraison(), "dateLivraison");
        ValidationCasUsage.texteObligatoire(demande.espaceConfluence(), "espaceConfluence");
        ValidationCasUsage.collectionObligatoire(demande.applicatifs(), "applicatifs");
        ValidationCasUsage.objetObligatoire(demande.cible(), "cible");
        ValidationCasUsage.texteObligatoire(auteur, "auteur");

        var analyses = analyserLivraison.executer(new DemandeAnalyseLivraison(demande.applicatifs(), demande.cible()));
        var recapitulatif = genererRecapitulatifLivraison.executer(analyses);
        var livraison = portMongo.sauvegarderLivraison(new Livraison(
                UUID.randomUUID(),
                demande.libelle(),
                demande.dateLivraison(),
                demande.description(),
                demande.cible(),
                demande.applicatifs(),
                analyses,
                LocalDateTime.now()
        ));

        if (!recapitulatif.jiras().isEmpty() && etiquetteLivraison != null && !etiquetteLivraison.isBlank()) {
            portJira.ajouterEtiquetteLivraison(recapitulatif.jiras(), etiquetteLivraison);
        }

        var ficheLivraison = portConfluence.creerFicheLivraison(demande, analyses, auteur);
        var ficheSauvegardee = portMongo.sauvegarderFicheLivraison(ficheLivraison);

        return new LivraisonGeneree(livraison, ficheSauvegardee, recapitulatif);
    }
}
