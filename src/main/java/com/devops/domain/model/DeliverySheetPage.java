package com.devops.domain.model;

import java.util.List;

public record DeliverySheetPage(
        List<DeliverySheetSummary> items,
        int page,
        int pageSize,
        long total
) {
    public DeliverySheetPage {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
