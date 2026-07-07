package com.devops.domain.port;

import com.devops.domain.model.ApplicationConfigurationFile;
import com.devops.domain.model.CommitInfo;
import com.devops.domain.model.ComparisonReference;
import com.devops.domain.model.RepositoryCoordinates;
import com.devops.domain.model.SourceFileChange;
import com.devops.domain.model.SourceReference;

import java.util.List;
import java.util.Optional;

public interface SourceControlPort {

    List<CommitInfo> listCommitsBetween(
            RepositoryCoordinates repository,
            ComparisonReference fromReference,
            ComparisonReference toReference
    );

    Optional<ApplicationConfigurationFile> readApplicationConfiguration(
            RepositoryCoordinates repository,
            ComparisonReference reference
    );

    List<SourceFileChange> listChangedFiles(RepositoryCoordinates repository, String commitHash);

    List<SourceReference> listBranches(RepositoryCoordinates repository);

    List<SourceReference> listTags(RepositoryCoordinates repository);
}
