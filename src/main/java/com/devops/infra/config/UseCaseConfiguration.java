package com.devops.infra.config;

import com.devops.domain.port.ConfluenceMetadataPort;
import com.devops.domain.port.DeliverySheetDocumentPort;
import com.devops.domain.port.DeliverySheetPort;
import com.devops.domain.port.DeploymentConfigurationPort;
import com.devops.domain.port.IssueTrackerPort;
import com.devops.domain.port.IssueLabelPort;
import com.devops.domain.port.RepositoryCatalogPort;
import com.devops.domain.port.RepositoryGroupPort;
import com.devops.domain.port.SourceControlPort;
import com.devops.domain.usecase.AddJiraIssueLabelsUseCase;
import com.devops.domain.usecase.AnalyzeApplicationsUseCase;
import com.devops.domain.usecase.GetConfluenceMetadataUseCase;
import com.devops.domain.usecase.GetRepositoryDetailsUseCase;
import com.devops.domain.usecase.ManageDeliverySheetsUseCase;
import com.devops.domain.usecase.ManageRepositoryGroupsUseCase;
import com.devops.domain.usecase.PrepareDeliverySummaryUseCase;
import com.devops.domain.usecase.SearchRepositoriesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    SearchRepositoriesUseCase searchRepositoriesUseCase(RepositoryCatalogPort repositoryCatalogPort) {
        return new SearchRepositoriesUseCase(repositoryCatalogPort);
    }

    @Bean
    GetRepositoryDetailsUseCase getRepositoryDetailsUseCase(RepositoryCatalogPort repositoryCatalogPort) {
        return new GetRepositoryDetailsUseCase(repositoryCatalogPort);
    }

    @Bean
    AnalyzeApplicationsUseCase analyzeApplicationsUseCase(
            SourceControlPort sourceControlPort,
            DeploymentConfigurationPort deploymentConfigurationPort,
            IssueTrackerPort issueTrackerPort
    ) {
        return new AnalyzeApplicationsUseCase(sourceControlPort, deploymentConfigurationPort, issueTrackerPort);
    }

    @Bean
    PrepareDeliverySummaryUseCase prepareDeliverySummaryUseCase() {
        return new PrepareDeliverySummaryUseCase();
    }

    @Bean
    ManageRepositoryGroupsUseCase manageRepositoryGroupsUseCase(
            RepositoryGroupPort repositoryGroupPort,
            RepositoryCatalogPort repositoryCatalogPort
    ) {
        return new ManageRepositoryGroupsUseCase(repositoryGroupPort, repositoryCatalogPort);
    }

    @Bean
    ManageDeliverySheetsUseCase manageDeliverySheetsUseCase(
            DeliverySheetPort deliverySheetPort,
            DeliverySheetDocumentPort deliverySheetDocumentPort
    ) {
        return new ManageDeliverySheetsUseCase(deliverySheetPort, deliverySheetDocumentPort);
    }

    @Bean
    GetConfluenceMetadataUseCase getConfluenceMetadataUseCase(ConfluenceMetadataPort confluenceMetadataPort) {
        return new GetConfluenceMetadataUseCase(confluenceMetadataPort);
    }

    @Bean
    AddJiraIssueLabelsUseCase addJiraIssueLabelsUseCase(IssueLabelPort issueLabelPort) {
        return new AddJiraIssueLabelsUseCase(issueLabelPort);
    }
}
