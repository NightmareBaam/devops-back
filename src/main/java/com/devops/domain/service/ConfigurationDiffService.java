package com.devops.domain.service;

import com.devops.domain.model.ConfigurationChangeType;
import com.devops.domain.model.ConfigurationDiff;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ConfigurationDiffService {

    public Set<ConfigurationDiff> diff(String file, Map<String, String> before, Map<String, String> after) {
        Map<String, String> previousValues = before == null ? Map.of() : before;
        Map<String, String> nextValues = after == null ? Map.of() : after;

        Set<String> keys = new TreeSet<>();
        keys.addAll(previousValues.keySet());
        keys.addAll(nextValues.keySet());

        Set<ConfigurationDiff> diffs = new TreeSet<>((left, right) -> left.key().compareTo(right.key()));
        for (String key : keys) {
            boolean existedBefore = previousValues.containsKey(key);
            boolean existsAfter = nextValues.containsKey(key);
            String previousValue = previousValues.get(key);
            String nextValue = nextValues.get(key);

            if (!existedBefore && existsAfter) {
                diffs.add(new ConfigurationDiff(file, key, null, nextValue, ConfigurationChangeType.ADDED));
            } else if (existedBefore && !existsAfter) {
                diffs.add(new ConfigurationDiff(file, key, previousValue, null, ConfigurationChangeType.REMOVED));
            } else if (!Objects.equals(previousValue, nextValue)) {
                diffs.add(new ConfigurationDiff(file, key, previousValue, nextValue, ConfigurationChangeType.MODIFIED));
            }
        }
        return Set.copyOf(diffs);
    }
}
