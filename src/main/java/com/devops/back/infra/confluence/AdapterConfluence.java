package com.devops.back.infra.confluence;

import com.devops.back.domain.livraison.modele.AnalyseLivraison;
import com.devops.back.domain.livraison.modele.DemandeGenerationLivraison;
import com.devops.back.domain.fla.modele.FicheLivraison;
import com.devops.back.domain.port.PortConfluence;
import com.devops.back.infra.ClientRestSupport;
import com.devops.back.infra.ExceptionInfrastructure;
import com.devops.back.infra.confluence.model.PageConfluenceRest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class AdapterConfluence implements PortConfluence {

    private final RestClient client;
    private final String baseUrl;

    public AdapterConfluence(
            @Value("${applications.confluence.base-url}") String baseUrl,
            @Value("${applications.confluence.username:}") String username,
            @Value("${applications.confluence.password:}") String password
    ) {
        this.baseUrl = baseUrl;
        this.client = ClientRestSupport.creer(baseUrl, username, password);
    }

    @Override
    public FicheLivraison creerFicheLivraison(
            DemandeGenerationLivraison demande,
            List<AnalyseLivraison> analyses,
            String auteur
    ) {
        try {
            var reponse = client.post()
                    .uri("/rest/api/content")
                    .body(contenuPage(demande, analyses))
                    .retrieve()
                    .body(PageConfluenceRest.class);

            return new FicheLivraison(
                    demande.libelle(),
                    urlPage(reponse),
                    LocalDateTime.now(),
                    auteur,
                    null,
                    null
            );
        } catch (RestClientException exception) {
            throw new ExceptionInfrastructure("Impossible de creer la fiche de livraison Confluence.", exception);
        }
    }

    private Map<String, Object> contenuPage(DemandeGenerationLivraison demande, List<AnalyseLivraison> analyses) {
        return Map.of(
                "type", "page",
                "title", demande.libelle(),
                "space", Map.of("key", demande.espaceConfluence()),
                "body", Map.of(
                        "storage", Map.of(
                                "value", contenuHtml(demande, analyses),
                                "representation", "storage"
                        )
                )
        );
    }

    private String contenuHtml(DemandeGenerationLivraison demande, List<AnalyseLivraison> analyses) {
        var contenu = new StringBuilder();
        contenu.append("<h1>").append(echapper(demande.libelle())).append("</h1>");
        contenu.append("<p>").append(echapper(demande.description())).append("</p>");
        contenu.append("<table><tbody>");
        contenu.append("<tr><th>Projet</th><th>Repository</th><th>Statut</th><th>Commits</th><th>Jiras</th></tr>");
        for (AnalyseLivraison analyse : analyses) {
            contenu.append("<tr>")
                    .append("<td>").append(echapper(analyse.projet())).append("</td>")
                    .append("<td>").append(echapper(analyse.slug())).append("</td>")
                    .append("<td>").append(analyse.statut()).append("</td>")
                    .append("<td>").append(analyse.commits() == null ? 0 : analyse.commits().nombre()).append("</td>")
                    .append("<td>").append(echapper(String.join(", ", analyse.jiras()))).append("</td>")
                    .append("</tr>");
        }
        contenu.append("</tbody></table>");
        return contenu.toString();
    }

    private String echapper(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String urlPage(PageConfluenceRest reponse) {
        if (reponse == null || reponse.links() == null || reponse.links().webui() == null) {
            return baseUrl;
        }
        var chemin = reponse.links().webui();
        return chemin.startsWith("http") ? chemin : baseUrl + chemin;
    }

}
