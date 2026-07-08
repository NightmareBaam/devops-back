package com.devops.back.infra.bitbucket;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.devops.back.domain.livraison.modele.ApplicatifAnalyse;
import com.devops.back.domain.livraison.modele.CommitAnalyse;
import com.devops.back.domain.livraison.modele.ConfigurationAnalyse;
import com.devops.back.domain.commun.modele.DetailRepositoryBitbucket;
import com.devops.back.domain.commun.modele.ReferenceVersion;
import com.devops.back.domain.commun.modele.RepositoryBitbucket;
import com.devops.back.domain.livraison.modele.ResumeCommits;
import com.devops.back.domain.livraison.modele.StatutAnalyseLivraison;
import com.devops.back.domain.commun.modele.TypeReference;
import com.devops.back.infra.ClientRestSupport;
import com.devops.back.infra.ExceptionInfrastructure;
import com.devops.back.infra.bitbucket.model.ChangementBitbucketRest;
import com.devops.back.infra.bitbucket.model.CommitBitbucketRest;
import com.devops.back.infra.bitbucket.model.PageBitbucket;
import com.devops.back.infra.bitbucket.model.ReferenceBitbucketRest;
import com.devops.back.infra.bitbucket.model.RepositoryBitbucketRest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashSet;
import java.util.List;

@Component
public class AdapterBitbucket implements com.devops.back.domain.port.PortBitbucket {

    private final RestClient client;
    private final RegistreDeploiementApp registreDeploiementApp;
    private final ComparateurYaml comparateurYaml;

    public AdapterBitbucket(
            @Value("${applications.bitbucket.base-url}") String baseUrl,
            @Value("${applications.bitbucket.username:}") String username,
            @Value("${applications.bitbucket.password:}") String password,
            RegistreDeploiementApp registreDeploiementApp,
            ComparateurYaml comparateurYaml
    ) {
        this.client = ClientRestSupport.creer(baseUrl, username, password);
        this.registreDeploiementApp = registreDeploiementApp;
        this.comparateurYaml = comparateurYaml;
    }

    @Override
    public List<RepositoryBitbucket> rechercherRepositories(String nom) {
        try {
            var reponse = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/rest/api/1.0/repos")
                            .queryParam("name", nom)
                            .queryParam("limit", 50)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageBitbucket<RepositoryBitbucketRest>>() {
                    });

            return reponse == null || reponse.values() == null
                    ? List.of()
                    : reponse.values().stream()
                    .map(repository -> new RepositoryBitbucket(repository.project().key(), repository.slug()))
                    .toList();
        } catch (RestClientException exception) {
            throw new ExceptionInfrastructure("Impossible de rechercher les repositories Bitbucket.", exception);
        }
    }

    @Override
    public DetailRepositoryBitbucket recupererDetailRepository(String projet, String slug) {
        try {
            var repository = client.get()
                    .uri("/rest/api/1.0/projects/{projet}/repos/{slug}", projet, slug)
                    .retrieve()
                    .body(RepositoryBitbucketRest.class);

            var nom = repository == null ? slug : repository.name();
            return new DetailRepositoryBitbucket(projet, slug, nom, registreDeploiementApp.versionsPour(projet, slug));
        } catch (RestClientException exception) {
            throw new ExceptionInfrastructure("Impossible de recuperer le detail du repository Bitbucket.", exception);
        }
    }

    @Override
    public List<String> recupererBranches(String projet, String slug) {
        return recupererNomsReferences("/rest/api/1.0/projects/{projet}/repos/{slug}/branches", projet, slug);
    }

    @Override
    public List<String> recupererTags(String projet, String slug) {
        return recupererNomsReferences("/rest/api/1.0/projects/{projet}/repos/{slug}/tags", projet, slug);
    }

    @Override
    public AnalyseLivraison comparerApplicatif(ApplicatifAnalyse applicatif, ReferenceVersion cible) {
        var base = resoudreReference(applicatif.base(), applicatif.projet(), applicatif.slug());
        var destination = resoudreReference(cible, applicatif.projet(), applicatif.slug());
        var commits = recupererCommits(applicatif.projet(), applicatif.slug(), base, destination);
        var changements = recupererChangements(applicatif.projet(), applicatif.slug(), base, destination);
        var configuration = analyserConfiguration(applicatif, base, destination, changements);
        var jiras = new LinkedHashSet<String>();
        commits.forEach(commit -> jiras.addAll(commit.jiras()));

        return new AnalyseLivraison(
                applicatif.projet(),
                applicatif.slug(),
                commits.isEmpty() ? StatutAnalyseLivraison.A_JOUR : StatutAnalyseLivraison.A_LIVRER,
                new ResumeCommits(commits.size(), commits),
                configuration,
                changements.size(),
                List.copyOf(jiras)
        );
    }

    private List<String> recupererNomsReferences(String chemin, String projet, String slug) {
        try {
            var reponse = client.get()
                    .uri(chemin, projet, slug)
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageBitbucket<ReferenceBitbucketRest>>() {
                    });
            return reponse == null || reponse.values() == null
                    ? List.of()
                    : reponse.values().stream().map(ReferenceBitbucketRest::displayId).toList();
        } catch (RestClientException exception) {
            throw new ExceptionInfrastructure("Impossible de recuperer les references Bitbucket.", exception);
        }
    }

    private String resoudreReference(ReferenceVersion reference, String projet, String slug) {
        if (reference.type() == TypeReference.ENVIRONNEMENT) {
            return registreDeploiementApp.versionPour(reference.valeur(), projet, slug)
                    .orElseThrow(() -> new ExceptionInfrastructure("Aucune version trouvee pour l'environnement " + reference.valeur() + "."));
        }
        return reference.valeur();
    }

    private List<CommitAnalyse> recupererCommits(String projet, String slug, String base, String destination) {
        try {
            var reponse = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/1.0/projects/{projet}/repos/{slug}/compare/commits")
                            .queryParam("from", destination)
                            .queryParam("to", base)
                            .queryParam("limit", 500)
                            .build(projet, slug))
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageBitbucket<CommitBitbucketRest>>() {
                    });

            return reponse == null || reponse.values() == null
                    ? List.of()
                    : reponse.values().stream()
                    .map(commit -> new CommitAnalyse(
                            commit.displayId(),
                            commit.message(),
                            commit.auteur(),
                            List.of(),
                            false
                    ))
                    .toList();
        } catch (RestClientException exception) {
            throw new ExceptionInfrastructure("Impossible de comparer les commits Bitbucket.", exception);
        }
    }

    private List<ChangementBitbucketRest> recupererChangements(String projet, String slug, String base, String destination) {
        try {
            var reponse = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/1.0/projects/{projet}/repos/{slug}/compare/changes")
                            .queryParam("from", destination)
                            .queryParam("to", base)
                            .queryParam("limit", 500)
                            .build(projet, slug))
                    .retrieve()
                    .body(new ParameterizedTypeReference<PageBitbucket<ChangementBitbucketRest>>() {
                    });
            return reponse == null || reponse.values() == null ? List.of() : reponse.values();
        } catch (RestClientException exception) {
            throw new ExceptionInfrastructure("Impossible de recuperer les fichiers modifies Bitbucket.", exception);
        }
    }

    private ConfigurationAnalyse analyserConfiguration(
            ApplicatifAnalyse applicatif,
            String base,
            String destination,
            List<ChangementBitbucketRest> changements
    ) {
        var configurationModifiee = changements.stream()
                .map(ChangementBitbucketRest::chemin)
                .anyMatch(chemin -> chemin.endsWith("application.yml") || chemin.endsWith("application.yaml"));
        if (!configurationModifiee) {
            return new ConfigurationAnalyse(false, List.of());
        }

        var cheminConfiguration = registreDeploiementApp.cheminConfigurationPour(applicatif.projet(), applicatif.slug());
        if (cheminConfiguration.isEmpty()) {
            return new ConfigurationAnalyse(true, List.of());
        }

        var ancienYaml = recupererFichierBrut(applicatif.projet(), applicatif.slug(), cheminConfiguration.get(), destination);
        var nouveauYaml = recupererFichierBrut(applicatif.projet(), applicatif.slug(), cheminConfiguration.get(), base);
        var differences = comparateurYaml.comparer(ancienYaml, nouveauYaml);
        return new ConfigurationAnalyse(true, differences);
    }

    private String recupererFichierBrut(String projet, String slug, String chemin, String reference) {
        try {
            return client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/1.0/projects/{projet}/repos/{slug}/raw/{chemin}")
                            .queryParam("at", reference)
                            .build(projet, slug, chemin))
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException exception) {
            return "";
        }
    }

}
