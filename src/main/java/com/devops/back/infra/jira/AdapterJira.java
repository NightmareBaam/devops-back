package com.devops.back.infra.jira;

import com.devops.back.domain.commun.modele.TicketJira;
import com.devops.back.domain.port.PortJira;
import com.devops.back.infra.ClientRestSupport;
import com.devops.back.infra.ExceptionInfrastructure;
import com.devops.back.infra.jira.model.IssueJiraRest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
public class AdapterJira implements PortJira {

    private final RestClient client;

    public AdapterJira(
            @Value("${applications.jira.base-url}") String baseUrl,
            @Value("${applications.jira.username:}") String username,
            @Value("${applications.jira.password:}") String password
    ) {
        this.client = ClientRestSupport.creer(baseUrl, username, password);
    }

    @Override
    public List<TicketJira> recupererTickets(List<String> cles) {
        if (cles == null || cles.isEmpty()) {
            return List.of();
        }
        return cles.stream()
                .map(this::recupererTicket)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public void ajouterEtiquetteLivraison(List<String> cles, String etiquette) {
        if (cles == null || cles.isEmpty() || etiquette == null || etiquette.isBlank()) {
            return;
        }
        cles.forEach(cle -> ajouterEtiquette(cle, etiquette));
    }

    private List<TicketJira> recupererTicket(String cle) {
        try {
            var issue = client.get()
                    .uri("/rest/api/2/issue/{cle}", cle)
                    .retrieve()
                    .body(IssueJiraRest.class);
            if (issue == null || issue.fields() == null) {
                return List.of();
            }
            return List.of(new TicketJira(
                    issue.key(),
                    issue.fields().summary(),
                    issue.fields().statut(),
                    issue.fields().responsable(),
                    issue.fields().labels() == null ? List.of() : issue.fields().labels()
            ));
        } catch (RestClientException exception) {
            return List.of();
        }
    }

    private void ajouterEtiquette(String cle, String etiquette) {
        var ticket = recupererTicket(cle).stream().findFirst();
        if (ticket.isEmpty()) {
            return;
        }
        var etiquettes = new LinkedHashSet<>(ticket.get().etiquettes());
        etiquettes.add(etiquette);

        try {
            client.put()
                    .uri("/rest/api/2/issue/{cle}", cle)
                    .body(Map.of("fields", Map.of("labels", List.copyOf(etiquettes))))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new ExceptionInfrastructure("Impossible de mettre a jour le ticket Jira " + cle + ".", exception);
        }
    }

}
