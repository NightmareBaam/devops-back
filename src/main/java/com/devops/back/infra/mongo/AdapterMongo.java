package com.devops.back.infra.mongo;

import com.devops.back.domain.fla.modele.FicheLivraison;
import com.devops.back.domain.groupe.modele.Groupe;
import com.devops.back.domain.livraison.modele.Livraison;
import com.devops.back.domain.port.PortMongo;
import com.devops.back.infra.mongo.model.FicheLivraisonDocument;
import com.devops.back.infra.mongo.model.GroupeDocument;
import com.devops.back.infra.mongo.model.LivraisonDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AdapterMongo implements PortMongo {

    private final RepositoryGroupeMongo repositoryGroupeMongo;
    private final RepositoryLivraisonMongo repositoryLivraisonMongo;
    private final RepositoryFicheLivraisonMongo repositoryFicheLivraisonMongo;
    private final MongoTemplate mongoTemplate;

    public AdapterMongo(
            RepositoryGroupeMongo repositoryGroupeMongo,
            RepositoryLivraisonMongo repositoryLivraisonMongo,
            RepositoryFicheLivraisonMongo repositoryFicheLivraisonMongo,
            MongoTemplate mongoTemplate
    ) {
        this.repositoryGroupeMongo = repositoryGroupeMongo;
        this.repositoryLivraisonMongo = repositoryLivraisonMongo;
        this.repositoryFicheLivraisonMongo = repositoryFicheLivraisonMongo;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Groupe> rechercherGroupes(int start, int limit) {
        return mongoTemplate.find(pagination(start, limit), GroupeDocument.class).stream()
                .map(MapperMongo::versDomaine)
                .toList();
    }

    @Override
    public Optional<Groupe> recupererGroupe(UUID identifiant) {
        return repositoryGroupeMongo.findById(identifiant).map(MapperMongo::versDomaine);
    }

    @Override
    public Groupe sauvegarderGroupe(Groupe groupe) {
        return MapperMongo.versDomaine(repositoryGroupeMongo.save(MapperMongo.versDocument(groupe)));
    }

    @Override
    public List<Livraison> rechercherLivraisons(int start, int limit) {
        return mongoTemplate.find(pagination(start, limit), LivraisonDocument.class).stream()
                .map(MapperMongo::versDomaine)
                .toList();
    }

    @Override
    public Optional<Livraison> recupererLivraison(UUID identifiant) {
        return repositoryLivraisonMongo.findById(identifiant).map(MapperMongo::versDomaine);
    }

    @Override
    public Livraison sauvegarderLivraison(Livraison livraison) {
        return MapperMongo.versDomaine(repositoryLivraisonMongo.save(MapperMongo.versDocument(livraison)));
    }

    @Override
    public List<FicheLivraison> rechercherFichesLivraison(int start, int limit) {
        return mongoTemplate.find(pagination(start, limit), FicheLivraisonDocument.class).stream()
                .map(MapperMongo::versDomaine)
                .toList();
    }

    @Override
    public FicheLivraison sauvegarderFicheLivraison(FicheLivraison ficheLivraison) {
        return MapperMongo.versDomaine(repositoryFicheLivraisonMongo.save(MapperMongo.versDocument(ficheLivraison)));
    }

    private Query pagination(int start, int limit) {
        return new Query().skip(start).limit(limit);
    }
}
