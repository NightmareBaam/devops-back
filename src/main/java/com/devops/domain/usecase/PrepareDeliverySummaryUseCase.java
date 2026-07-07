package com.devops.domain.usecase;

import com.devops.domain.model.ApplicationAnalysis;
import com.devops.domain.model.DeliverySummary;
import com.devops.domain.service.DeliverySummaryPreparer;

import java.util.List;
import java.util.Objects;

public class PrepareDeliverySummaryUseCase {

    private final DeliverySummaryPreparer deliverySummaryPreparer;

    public PrepareDeliverySummaryUseCase() {
        this(new DeliverySummaryPreparer());
    }

    public PrepareDeliverySummaryUseCase(DeliverySummaryPreparer deliverySummaryPreparer) {
        this.deliverySummaryPreparer = Objects.requireNonNull(deliverySummaryPreparer, "deliverySummaryPreparer must not be null");
    }

    public DeliverySummary prepare(List<ApplicationAnalysis> selectedAnalyses) {
        return deliverySummaryPreparer.prepare(selectedAnalyses);
    }
}
