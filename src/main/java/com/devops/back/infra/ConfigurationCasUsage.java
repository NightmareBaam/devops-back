package com.devops.back.infra;

import com.devops.back.domain.fla.casusage.RechercherFichesLivraison;
import com.devops.back.domain.groupe.casusage.GererGroupes;
import com.devops.back.domain.groupe.casusage.RechercherRepositories;
import com.devops.back.domain.groupe.casusage.RecupererDetailRepository;
import com.devops.back.domain.livraison.casusage.AnalyserLivraison;
import com.devops.back.domain.livraison.casusage.GenererLivraison;
import com.devops.back.domain.port.PortBitbucket;
import com.devops.back.domain.port.PortConfluence;
import com.devops.back.domain.port.PortJira;
import com.devops.back.domain.port.PortMongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationCasUsage {

    @Bean
    public RechercherFichesLivraison rechercherFichesLivraison(PortMongo portMongo) {
        return new RechercherFichesLivraison(portMongo);
    }

    @Bean
    public RechercherRepositories rechercherRepositories(PortBitbucket portBitbucket) {
        return new RechercherRepositories(portBitbucket);
    }

    @Bean
    public RecupererDetailRepository recupererDetailRepository(PortBitbucket portBitbucket) {
        return new RecupererDetailRepository(portBitbucket);
    }

    @Bean
    public GererGroupes gererGroupes(PortMongo portMongo) {
        return new GererGroupes(portMongo);
    }

    @Bean
    public AnalyserLivraison analyserLivraison(PortBitbucket portBitbucket) {
        return new AnalyserLivraison(portBitbucket);
    }

    @Bean
    public GenererLivraison genererLivraison(
            AnalyserLivraison analyserLivraison,
            PortJira portJira,
            PortConfluence portConfluence,
            PortMongo portMongo
    ) {
        return new GenererLivraison(analyserLivraison, portJira, portConfluence, portMongo);
    }
}
