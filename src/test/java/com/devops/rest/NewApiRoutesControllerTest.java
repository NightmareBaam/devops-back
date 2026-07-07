package com.devops.rest;

import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.ApplicationRepository;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.ConfluenceSpace;
import com.devops.domain.model.ConfluenceTemplate;
import com.devops.domain.model.DeliverySheet;
import com.devops.domain.model.DeliverySheetCreationCommand;
import com.devops.domain.model.DeploymentApplicationVersion;
import com.devops.domain.model.Environment;
import com.devops.domain.model.JiraIssueInfo;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryGroup;
import com.devops.domain.model.RepositoryVersion;
import com.devops.domain.model.SourceFileChange;
import com.devops.domain.model.SourceReference;
import com.devops.domain.model.SourceReferenceType;
import com.devops.domain.port.ConfluenceMetadataPort;
import com.devops.domain.port.DeliverySheetDocumentPort;
import com.devops.domain.port.DeliverySheetPort;
import com.devops.domain.port.DeploymentConfigurationPort;
import com.devops.domain.port.IssueLabelPort;
import com.devops.domain.port.IssueTrackerPort;
import com.devops.domain.port.RepositoryCatalogPort;
import com.devops.domain.port.RepositoryGroupPort;
import com.devops.domain.port.SourceControlPort;
import com.devops.domain.usecase.AddJiraIssueLabelsUseCase;
import com.devops.rest.controller.ConfluenceMetadataController;
import com.devops.rest.controller.DeliveryAnalysisCompareController;
import com.devops.rest.controller.DeliverySheetController;
import com.devops.rest.controller.JiraIssueController;
import com.devops.rest.controller.RepositoryGroupController;
import com.devops.rest.controller.RepositoryReferencesController;
import com.devops.domain.usecase.GetConfluenceMetadataUseCase;
import com.devops.domain.usecase.ManageDeliverySheetsUseCase;
import com.devops.domain.usecase.ManageRepositoryGroupsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NewApiRoutesControllerTest {

    @Test
    void exposesRepositoryGroupCrudRoutes() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new RepositoryGroupController(
                new ManageRepositoryGroupsUseCase(new InMemoryRepositoryGroupPort(), new StubRepositoryCatalogPort())
        )).build();
        String payload = """
                {
                  "name": "Paiement",
                  "description": "Applications paiement",
                  "repositories": [{"projectKey": "PAY", "slug": "payment-api", "order": 1}],
                  "defaultEnvironment": "DEV",
                  "defaultTag": null
                }
                """;

        String groupJson = mockMvc.perform(post("/api/repository-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Paiement"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String groupId = groupJson.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/repository-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(groupId));
        mockMvc.perform(get("/api/repository-groups/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].repository.slug").value("payment-api"));
        mockMvc.perform(delete("/api/repository-groups/{groupId}", groupId))
                .andExpect(status().isNoContent());
    }

    @Test
    void exposesRepositoryReferencesRoute() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new RepositoryReferencesController(
                new StubSourceControlPort(),
                new StubDeploymentConfigurationPort()
        )).build();

        mockMvc.perform(get("/api/repositories/PAY/payment-api/references"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repository.projectKey").value("PAY"))
                .andExpect(jsonPath("$.branches[0]").value("develop"))
                .andExpect(jsonPath("$.tags[0]").value("release/1.7.0"))
                .andExpect(jsonPath("$.environments[0].environment").value("REC"));
    }

    @Test
    void exposesCompareAnalysisRoute() throws Exception {
        DeliveryAnalysisCompareService service = new DeliveryAnalysisCompareService(
                new StubRepositoryCatalogPort(),
                new StubSourceControlPort(),
                new StubDeploymentConfigurationPort(),
                new StubIssueTrackerPort()
        );
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new DeliveryAnalysisCompareController(service)).build();
        String payload = """
                {
                  "applications": [
                    {
                      "repository": {"projectKey": "PAY", "slug": "payment-api"},
                      "baseReference": {"type": "BRANCH", "value": "develop"},
                      "targetReference": {"type": "ENVIRONMENT", "environment": "REC", "value": "REC"}
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/delivery-analyses/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repository.slug").value("payment-api"))
                .andExpect(jsonPath("$[0].commitCount").value(1))
                .andExpect(jsonPath("$[0].jiraIssueKeys[0]").value("PAY-123"));
    }

    @Test
    void exposesDeliverySheetRoutes() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new DeliverySheetController(
                new ManageDeliverySheetsUseCase(new InMemoryDeliverySheetPort(), new StubDeliverySheetDocumentPort())
        )).build();
        String payload = """
                {
                  "title": "Livraison Paiement",
                  "groupId": "grp-payment",
                  "targetEnvironment": "PPR",
                  "deliveryDate": "2026-07-03",
                  "responsible": "Marie Dupont",
                  "jiraEpicKey": "PAY-1",
                  "description": "Livraison",
                  "confluence": {"spaceKey": "FLA", "templateId": "tpl", "visibility": "TEAM"},
                  "options": {"includeCommitLinks": true, "includeJiraBuildLinks": true, "includeBitbucketLinks": true},
                  "selectedApplications": [{"projectKey": "PAY", "slug": "payment-api"}],
                  "jiraIssueKeys": ["PAY-123"]
                }
                """;

        String created = mockMvc.perform(post("/api/delivery-sheets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jiraIssueKeys[0]").value("PAY-123"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String sheetId = created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/delivery-sheets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(sheetId));
        mockMvc.perform(get("/api/delivery-sheets/{deliverySheetId}", sheetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Livraison Paiement"));
        mockMvc.perform(post("/api/delivery-sheets/{deliverySheetId}/duplicate", sheetId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void exposesConfluenceAndJiraRoutes() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new ConfluenceMetadataController(new GetConfluenceMetadataUseCase(new StubConfluenceMetadataPort())),
                new JiraIssueController(new AddJiraIssueLabelsUseCase(new CapturingIssueLabelPort()))
        ).build();

        mockMvc.perform(get("/api/confluence/spaces"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/confluence/templates").param("spaceKey", "FLA"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/jira/issues/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueKeys\":[\"PAY-123\"],\"labels\":[\"livraison-ppr\"]}"))
                .andExpect(status().isNoContent());
    }

    private static class InMemoryRepositoryGroupPort implements RepositoryGroupPort {
        private final Map<String, RepositoryGroup> groups = new LinkedHashMap<>();

        @Override
        public List<RepositoryGroup> findAll() {
            return List.copyOf(groups.values());
        }

        @Override
        public Optional<RepositoryGroup> findById(String id) {
            return Optional.ofNullable(groups.get(id));
        }

        @Override
        public RepositoryGroup save(RepositoryGroup group) {
            groups.put(group.id(), group);
            return group;
        }

        @Override
        public void delete(String id) {
            groups.remove(id);
        }
    }

    private static class InMemoryDeliverySheetPort implements DeliverySheetPort {
        private final Map<String, DeliverySheet> sheets = new LinkedHashMap<>();

        @Override
        public List<DeliverySheet> findAll() {
            return List.copyOf(sheets.values());
        }

        @Override
        public Optional<DeliverySheet> findById(String id) {
            return Optional.ofNullable(sheets.get(id));
        }

        @Override
        public DeliverySheet save(DeliverySheet sheet) {
            sheets.put(sheet.id(), sheet);
            return sheet;
        }
    }

    private static class StubDeliverySheetDocumentPort implements DeliverySheetDocumentPort {
        @Override
        public String createPage(String deliverySheetId, DeliverySheetCreationCommand command) {
            return "/display/FLA/" + deliverySheetId;
        }
    }

    private static class StubConfluenceMetadataPort implements ConfluenceMetadataPort {
        @Override
        public List<ConfluenceSpace> listSpaces() {
            return List.of(new ConfluenceSpace("FLA", "Fiches livraison"));
        }

        @Override
        public List<ConfluenceTemplate> listTemplates(String spaceKey) {
            return List.of(new ConfluenceTemplate("tpl", "Fiche livraison", spaceKey));
        }
    }

    private static class CapturingIssueLabelPort implements IssueLabelPort {
        @Override
        public void addLabels(Set<String> issueKeys, Set<String> labels) {
        }
    }

    private static class StubRepositoryCatalogPort implements RepositoryCatalogPort {
        @Override
        public List<ApplicationRepository> searchRepositories(String query, int limit) {
            return List.of();
        }

        @Override
        public Optional<ApplicationRepository> findRepository(RepositoryCoordinates repository) {
            return Optional.of(new ApplicationRepository(repository.projectKey(), repository.slug(), repository.slug(), "", "", List.of()));
        }

        @Override
        public List<RepositoryVersion> findVersions(RepositoryCoordinates repository) {
            return List.of();
        }
    }

    private static class StubSourceControlPort implements SourceControlPort {
        @Override
        public List<CommitInfo> listCommitsBetween(RepositoryCoordinates repository, ComparisonReference fromReference, ComparisonReference toReference) {
            return List.of(new CommitInfo("abc", "PAY-123 delivery", "alice", Instant.parse("2026-07-03T00:00:00Z"), Set.of(), false));
        }

        @Override
        public Optional<ApplicationConfigurationFile> readApplicationConfiguration(RepositoryCoordinates repository, ComparisonReference reference) {
            return Optional.of(new ApplicationConfigurationFile(repository, reference, "application.yml", "", Map.of("feature.enabled", "true")));
        }

        @Override
        public List<SourceFileChange> listChangedFiles(RepositoryCoordinates repository, String commitHash) {
            return List.of();
        }

        @Override
        public List<SourceReference> listBranches(RepositoryCoordinates repository) {
            return List.of(new SourceReference("develop", SourceReferenceType.BRANCH, "abc"));
        }

        @Override
        public List<SourceReference> listTags(RepositoryCoordinates repository) {
            return List.of(new SourceReference("release/1.7.0", SourceReferenceType.TAG, "def"));
        }
    }

    private static class StubDeploymentConfigurationPort implements DeploymentConfigurationPort {
        @Override
        public Map<Environment, List<DeploymentApplicationVersion>> findDeliveredApplicationsByEnvironment() {
            return Map.of();
        }

        @Override
        public List<RepositoryVersion> findDeliveredVersions(RepositoryCoordinates repository) {
            return List.of(new RepositoryVersion(Environment.REC, "release/1.7.0", Instant.parse("2026-06-24T10:15:00Z")));
        }

        @Override
        public Optional<RepositoryVersion> findDeliveredVersion(RepositoryCoordinates repository, Environment environment) {
            return Optional.of(new RepositoryVersion(environment, "release/1.7.0", null));
        }
    }

    private static class StubIssueTrackerPort implements IssueTrackerPort {
        @Override
        public Map<String, JiraIssueInfo> findIssuesByKeys(Set<String> issueKeys) {
            return Map.of("PAY-123", new JiraIssueInfo("PAY-123", "Ticket", "Open", "alice", Set.of()));
        }
    }
}
