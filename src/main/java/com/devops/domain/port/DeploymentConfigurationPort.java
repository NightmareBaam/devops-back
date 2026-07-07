package com.devops.domain.port;

import com.devops.domain.model.DeploymentApplicationVersion;
import com.devops.domain.model.Environment;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.RepositoryVersion;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DeploymentConfigurationPort {

    Map<Environment, List<DeploymentApplicationVersion>> findDeliveredApplicationsByEnvironment();

    List<RepositoryVersion> findDeliveredVersions(RepositoryCoordinates repository);

    Optional<RepositoryVersion> findDeliveredVersion(RepositoryCoordinates repository, Environment environment);
}
