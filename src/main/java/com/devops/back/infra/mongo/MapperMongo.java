package com.devops.back.infra.mongo;

import com.devops.back.domain.fla.modele.FicheLivraison;
import com.devops.back.domain.groupe.modele.Groupe;
import com.devops.back.domain.livraison.modele.Livraison;
import com.devops.back.infra.mongo.model.FicheLivraisonDocument;
import com.devops.back.infra.mongo.model.GroupeDocument;
import com.devops.back.infra.mongo.model.LivraisonDocument;

import java.util.UUID;

final class MapperMongo {

    private MapperMongo() {
    }

    static GroupeDocument versDocument(Groupe groupe) {
        return new GroupeDocument(
                groupe.identifiant(),
                groupe.libelle(),
                groupe.description(),
                groupe.base(),
                groupe.cible(),
                groupe.repositoriesBitbucket(),
                groupe.dateDerniereModification()
        );
    }

    static Groupe versDomaine(GroupeDocument document) {
        return new Groupe(
                document.identifiant(),
                document.libelle(),
                document.description(),
                document.base(),
                document.cible(),
                document.repositoriesBitbucket(),
                document.dateDerniereModification()
        );
    }

    static LivraisonDocument versDocument(Livraison livraison) {
        return new LivraisonDocument(
                livraison.identifiant(),
                livraison.libelle(),
                livraison.dateLivraison(),
                livraison.description(),
                livraison.cible(),
                livraison.applicatifs(),
                livraison.analyses(),
                livraison.dateCreation()
        );
    }

    static Livraison versDomaine(LivraisonDocument document) {
        return new Livraison(
                document.identifiant(),
                document.libelle(),
                document.dateLivraison(),
                document.description(),
                document.cible(),
                document.applicatifs(),
                document.analyses(),
                document.dateCreation()
        );
    }

    static FicheLivraisonDocument versDocument(FicheLivraison ficheLivraison) {
        return new FicheLivraisonDocument(
                ficheLivraison.livraison() == null ? UUID.randomUUID() : ficheLivraison.livraison(),
                ficheLivraison.libelle(),
                ficheLivraison.url(),
                ficheLivraison.date(),
                ficheLivraison.auteur(),
                ficheLivraison.groupe(),
                ficheLivraison.livraison()
        );
    }

    static FicheLivraison versDomaine(FicheLivraisonDocument document) {
        return new FicheLivraison(
                document.libelle(),
                document.url(),
                document.date(),
                document.auteur(),
                document.groupe(),
                document.livraison()
        );
    }
}
