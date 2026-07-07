package com.devops.infra.confluence;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.ConfluenceSpace;
import com.devops.domain.model.ConfluenceTemplate;
import com.devops.domain.model.DeliveryDocument;
import com.devops.domain.model.DeliveryDocumentStatus;
import com.devops.domain.model.DeliverySheetCreationCommand;
import com.devops.domain.model.DeliverySummary;
import com.devops.domain.model.RepositoryRef;
import com.devops.domain.port.ConfluenceMetadataPort;
import com.devops.domain.port.DeliveryDocumentPort;
import com.devops.domain.port.DeliverySheetDocumentPort;
import com.devops.infra.config.BasicAuthRestClientFactory;
import com.devops.infra.config.DeliveryDocumentProperties;
import com.devops.infra.config.IntegrationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class ConfluenceServerAdapter implements DeliveryDocumentPort, DeliverySheetDocumentPort, ConfluenceMetadataPort {

    private final RestClient restClient;
    private final DeliveryDocumentProperties properties;

    public ConfluenceServerAdapter(
            BasicAuthRestClientFactory restClientFactory,
            IntegrationProperties integrationProperties,
            DeliveryDocumentProperties properties
    ) {
        this.restClient = restClientFactory.create(integrationProperties.confluence());
        this.properties = properties;
    }

    @Override
    public DeliveryDocument prepareDocument(DeliverySummary summary) {
        return new DeliveryDocument(
                "",
                title(summary),
                properties.spaceKey(),
                "",
                content(summary),
                DeliveryDocumentStatus.PREPARED,
                null
        );
    }

    @Override
    public DeliveryDocument createDocument(DeliverySummary summary) {
        DeliveryDocument prepared = prepareDocument(summary);
        ConfluencePageResponse response = createPage(prepared.title(), prepared.spaceKey(), prepared.content());

        String id = response == null ? "" : response.id();
        String url = response == null ? "" : response.webUrl();
        return new DeliveryDocument(
                id,
                prepared.title(),
                prepared.spaceKey(),
                url,
                prepared.content(),
                DeliveryDocumentStatus.CREATED,
                Instant.now()
        );
    }

    public ConfluencePageResponse createPage(String title, String spaceKey, String storageContent) {
        ConfluenceCreatePageRequest request = new ConfluenceCreatePageRequest(
                "page",
                title,
                new ConfluenceSpaceRequest(spaceKey),
                new ConfluenceBodyRequest(new ConfluenceStorageRequest(storageContent, "storage"))
        );

        return restClient.post()
                .uri("/rest/api/content")
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }

    @Override
    public String createPage(String deliverySheetId, DeliverySheetCreationCommand command) {
        String spaceKey = command.confluence() == null || command.confluence().spaceKey() == null
                ? properties.spaceKey()
                : command.confluence().spaceKey();
        ConfluencePageResponse page = createPage(command.title(), spaceKey, deliverySheetContent(deliverySheetId, command));
        if (page == null || page.webUrl().isBlank()) {
            return "/display/" + spaceKey + "/" + deliverySheetId;
        }
        return page.webUrl();
    }

    @Override
    public List<ConfluenceSpace> listSpaces() {
        return listConfluenceSpaces().stream()
                .map(space -> new ConfluenceSpace(space.key(), space.name()))
                .toList();
    }

    public List<ConfluenceSpaceResponse> listConfluenceSpaces() {
        ConfluencePageResponsePage<ConfluenceSpaceResponse> response = restClient.get()
                .uri("/rest/api/space")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return response == null || response.results() == null ? List.of() : response.results();
    }

    @Override
    public List<ConfluenceTemplate> listTemplates(String spaceKey) {
        return listConfluenceTemplates(spaceKey).stream()
                .map(template -> new ConfluenceTemplate(template.templateId(), template.name(), spaceKey))
                .toList();
    }

    public List<ConfluenceTemplateResponse> listConfluenceTemplates(String spaceKey) {
        ConfluencePageResponsePage<ConfluenceTemplateResponse> response = restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/rest/api/template/page");
                    if (spaceKey != null && !spaceKey.isBlank()) {
                        builder.queryParam("spaceKey", spaceKey);
                    }
                    return builder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return response == null || response.results() == null ? List.of() : response.results();
    }

    private String deliverySheetContent(String id, DeliverySheetCreationCommand command) {
        StringBuilder content = new StringBuilder();
        content.append("<h1>").append(command.title()).append("</h1>");
        content.append("<p><strong>FLA:</strong> ").append(id).append("</p>");
        content.append("<p><strong>Groupe:</strong> ").append(command.groupId()).append("</p>");
        content.append("<p><strong>Environnement cible:</strong> ").append(command.targetEnvironment()).append("</p>");
        content.append("<p><strong>Responsable:</strong> ").append(command.responsible()).append("</p>");
        content.append("<p>").append(command.description() == null ? "" : command.description()).append("</p>");
        content.append("<h2>Applications</h2><ul>");
        for (RepositoryRef application : command.selectedApplications()) {
            content.append("<li>").append(application.projectKey()).append("/").append(application.slug()).append("</li>");
        }
        content.append("</ul><h2>Tickets Jira</h2><ul>");
        for (String jiraIssueKey : command.jiraIssueKeys()) {
            content.append("<li>").append(jiraIssueKey).append("</li>");
        }
        content.append("</ul>");
        return content.toString();
    }

    private String title(DeliverySummary summary) {
        return properties.titlePrefix() + " - " + summary.preparedAt();
    }

    private String content(DeliverySummary summary) {
        StringBuilder content = new StringBuilder();
        content.append("<h1>Fiche de livraison</h1>");
        content.append("<p>Total commits: ").append(summary.totalCommitCount()).append("</p>");
        content.append("<p>Tickets Jira: ").append(String.join(", ", summary.jiraIssueKeys())).append("</p>");
        content.append("<h2>Applications</h2>");
        content.append("<ul>");
        for (ApplicationAnalysis application : summary.applications()) {
            content.append("<li>")
                    .append(application.repository().projectKey())
                    .append("/")
                    .append(application.repository().slug())
                    .append(" - ")
                    .append(application.commitCount())
                    .append(" commit(s)")
                    .append("</li>");
        }
        content.append("</ul>");
        return content.toString();
    }

    record ConfluenceCreatePageRequest(
            String type,
            String title,
            ConfluenceSpaceRequest space,
            ConfluenceBodyRequest body
    ) {
    }

    record ConfluenceSpaceRequest(String key) {
    }

    record ConfluenceBodyRequest(ConfluenceStorageRequest storage) {
    }

    record ConfluenceStorageRequest(String value, String representation) {
    }

    public record ConfluencePageResponse(
            String id,
            Map<String, String> _links
    ) {

        public String webUrl() {
            if (_links == null) {
                return "";
            }
            String base = _links.getOrDefault("base", "");
            String webui = _links.getOrDefault("webui", "");
            return base + webui;
        }
    }

    public record ConfluencePageResponsePage<T>(
            List<T> results
    ) {
    }

    public record ConfluenceSpaceResponse(
            String key,
            String name
    ) {
    }

    public record ConfluenceTemplateResponse(
            String templateId,
            String name,
            String description
    ) {
    }
}
