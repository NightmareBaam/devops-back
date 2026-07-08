package com.devops.back.infra.bitbucket;

import com.devops.back.domain.commun.modele.VersionEnvironnement;
import com.devops.back.infra.bitbucket.model.ApplicationDeployee;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "applications.deploiement-app")
public class RegistreDeploiementApp {

    private Map<String, List<ApplicationDeployee>> environnements = new HashMap<>();

    public Map<String, List<ApplicationDeployee>> getEnvironnements() {
        return environnements;
    }

    public void setEnvironnements(Map<String, List<ApplicationDeployee>> environnements) {
        this.environnements = environnements == null ? new HashMap<>() : environnements;
    }

    public List<VersionEnvironnement> versionsPour(String projet, String slug) {
        return environnements.entrySet().stream()
                .flatMap(entree -> entree.getValue().stream()
                        .filter(application -> memeRepository(application, projet, slug))
                        .map(application -> new VersionEnvironnement(entree.getKey().toUpperCase(Locale.ROOT), application.version())))
                .toList();
    }

    public Optional<String> versionPour(String environnement, String projet, String slug) {
        return applicationsPour(environnement).stream()
                .filter(application -> memeRepository(application, projet, slug))
                .map(ApplicationDeployee::version)
                .filter(version -> version != null && !version.isBlank())
                .findFirst();
    }

    public Optional<String> cheminConfigurationPour(String projet, String slug) {
        return environnements.values().stream()
                .flatMap(List::stream)
                .filter(application -> memeRepository(application, projet, slug))
                .map(ApplicationDeployee::cheminConfiguration)
                .filter(chemin -> chemin != null && !chemin.isBlank())
                .findFirst();
    }

    private List<ApplicationDeployee> applicationsPour(String environnement) {
        if (environnement == null) {
            return List.of();
        }
        var applications = environnements.get(environnement.toLowerCase(Locale.ROOT));
        return applications == null ? List.of() : new ArrayList<>(applications);
    }

    private boolean memeRepository(ApplicationDeployee application, String projet, String slug) {
        return application.projet() != null
                && application.slug() != null
                && application.projet().equalsIgnoreCase(projet)
                && application.slug().equalsIgnoreCase(slug);
    }
}
