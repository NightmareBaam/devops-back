package com.devops.domain.port;

import com.devops.domain.model.DeliveryDocument;
import com.devops.domain.model.DeliverySummary;

public interface DeliveryDocumentPort {

    DeliveryDocument prepareDocument(DeliverySummary summary);

    DeliveryDocument createDocument(DeliverySummary summary);
}
