package com.devops.domain.port;

import java.util.Set;

public interface IssueLabelPort {

    void addLabels(Set<String> issueKeys, Set<String> labels);
}
