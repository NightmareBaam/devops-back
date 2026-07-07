package com.devops.infra.confluence;

import com.devops.domain.model.DeliveryDocument;
import com.devops.domain.model.DeliveryDocumentStatus;
import com.devops.domain.model.DeliverySummary;
import com.devops.infra.config.BasicAuthRestClientFactory;
import com.devops.infra.config.DeliveryDocumentProperties;
import com.devops.infra.config.IntegrationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ConfluenceServerAdapterTest {

    private MockRestServiceServer server;
    private ConfluenceServerAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new ConfluenceServerAdapter(
                new BasicAuthRestClientFactory(builder),
                properties(),
                new DeliveryDocumentProperties("FLA", "Livraison")
        );
    }

    @Test
    void preparesDocumentWithoutCallingConfluence() {
        DeliveryDocument document = adapter.prepareDocument(summary());

        assertThat(document.status()).isEqualTo(DeliveryDocumentStatus.PREPARED);
        assertThat(document.spaceKey()).isEqualTo("FLA");
        assertThat(document.title()).startsWith("Livraison - ");
        assertThat(document.content()).contains("Total commits: 2");
        server.verify();
    }

    @Test
    void createsConfluencePageAndMapsResponse() {
        server.expect(requestTo("http://confluence.local/rest/api/content"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"title\":\"Livraison - 2026-07-02T12:00:00Z\"")))
                .andRespond(withSuccess("""
                        {
                          "id": "12345",
                          "_links": {
                            "base": "http://confluence.local",
                            "webui": "/pages/viewpage.action?pageId=12345"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        DeliveryDocument document = adapter.createDocument(summary());

        assertThat(document.id()).isEqualTo("12345");
        assertThat(document.status()).isEqualTo(DeliveryDocumentStatus.CREATED);
        assertThat(document.url()).isEqualTo("http://confluence.local/pages/viewpage.action?pageId=12345");
        server.verify();
    }

    @Test
    void listsSpacesAndTemplates() {
        server.expect(requestTo("http://confluence.local/rest/api/space"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "results": [
                            { "key": "FLA", "name": "Fiches livraison" }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://confluence.local/rest/api/template/page?spaceKey=FLA"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "results": [
                            { "templateId": "tpl-delivery-standard", "name": "Livraison standard" }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        assertThat(adapter.listSpaces()).extracting("key").containsExactly("FLA");
        assertThat(adapter.listTemplates("FLA")).extracting("id").containsExactly("tpl-delivery-standard");
        server.verify();
    }

    private DeliverySummary summary() {
        return new DeliverySummary(List.of(), Set.of("ABC-123"), 2, false, Instant.parse("2026-07-02T12:00:00Z"));
    }

    private IntegrationProperties properties() {
        IntegrationProperties.Server unused = new IntegrationProperties.Server("http://unused.local", "", "");
        IntegrationProperties.Server confluence = new IntegrationProperties.Server("http://confluence.local", "user", "pass");
        return new IntegrationProperties(unused, unused, confluence);
    }
}
