package com.devops;

import com.devops.domain.model.DeliverySheet;
import com.devops.domain.model.RepositoryGroup;
import com.devops.domain.port.DeliverySheetPort;
import com.devops.domain.port.RepositoryGroupPort;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = "spring.autoconfigure.exclude="
        + "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
        + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
        + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration")
class DevopsApplicationTests {

    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class TestPortsConfiguration {

        @Bean
        RepositoryGroupPort repositoryGroupPort() {
            return new RepositoryGroupPort() {
                @Override
                public List<RepositoryGroup> findAll() {
                    return List.of();
                }

                @Override
                public Optional<RepositoryGroup> findById(String id) {
                    return Optional.empty();
                }

                @Override
                public RepositoryGroup save(RepositoryGroup group) {
                    return group;
                }

                @Override
                public void delete(String id) {
                }
            };
        }

        @Bean
        DeliverySheetPort deliverySheetPort() {
            return new DeliverySheetPort() {
                @Override
                public List<DeliverySheet> findAll() {
                    return List.of();
                }

                @Override
                public Optional<DeliverySheet> findById(String id) {
                    return Optional.empty();
                }

                @Override
                public DeliverySheet save(DeliverySheet sheet) {
                    return sheet;
                }
            };
        }
    }
}
