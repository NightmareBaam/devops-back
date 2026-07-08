package com.devops.back.domain.groupe.casusage;

import com.devops.back.domain.commun.casusage.ExceptionMetier;

import com.devops.back.domain.commun.casusage.ValidationCasUsage;

import com.devops.back.domain.groupe.modele.Groupe;
import com.devops.back.domain.port.PortMongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GererGroupes {

    private final PortMongo portMongo;

    public GererGroupes(PortMongo portMongo) {
        this.portMongo = portMongo;
    }

    public List<Groupe> rechercher(int start, int limit) {
        ValidationCasUsage.paginationValide(start, limit);
        return portMongo.rechercherGroupes(start, limit);
    }

    public Groupe recuperer(UUID identifiant) {
        return portMongo.recupererGroupe(ValidationCasUsage.identifiantObligatoire(identifiant, "identifiant"))
                .orElseThrow(() -> new ExceptionMetier("Le groupe demande est introuvable."));
    }

    public Groupe creer(Groupe groupe) {
        ValidationCasUsage.objetObligatoire(groupe, "groupe");
        var groupeACreer = new Groupe(
                groupe.identifiant() == null ? UUID.randomUUID() : groupe.identifiant(),
                ValidationCasUsage.texteObligatoire(groupe.libelle(), "libelle"),
                groupe.description(),
                ValidationCasUsage.objetObligatoire(groupe.base(), "base"),
                ValidationCasUsage.objetObligatoire(groupe.cible(), "cible"),
                List.copyOf(groupe.repositoriesBitbucket()),
                LocalDateTime.now()
        );
        return portMongo.sauvegarderGroupe(groupeACreer);
    }

    public Groupe modifier(UUID identifiant, Groupe groupe) {
        ValidationCasUsage.identifiantObligatoire(identifiant, "identifiant");
        ValidationCasUsage.objetObligatoire(groupe, "groupe");
        recuperer(identifiant);
        var groupeAModifier = new Groupe(
                identifiant,
                ValidationCasUsage.texteObligatoire(groupe.libelle(), "libelle"),
                groupe.description(),
                ValidationCasUsage.objetObligatoire(groupe.base(), "base"),
                ValidationCasUsage.objetObligatoire(groupe.cible(), "cible"),
                List.copyOf(groupe.repositoriesBitbucket()),
                LocalDateTime.now()
        );
        return portMongo.sauvegarderGroupe(groupeAModifier);
    }
}
