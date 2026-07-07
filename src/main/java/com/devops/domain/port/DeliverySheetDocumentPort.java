package com.devops.domain.port;

import com.devops.domain.model.DeliverySheetCreationCommand;

public interface DeliverySheetDocumentPort {

    String createPage(String deliverySheetId, DeliverySheetCreationCommand command);
}
