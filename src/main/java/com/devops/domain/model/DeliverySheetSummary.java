package com.devops.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public record DeliverySheetSummary(
        String id,
        String title,
        String groupId,
        String groupName,
        Environment targetEnvironment,
        String status,
        String author,
        LocalDate deliveryDate,
        Instant createdAt
) {
    public static DeliverySheetSummary from(DeliverySheet sheet) {
        return new DeliverySheetSummary(
                sheet.id(),
                sheet.title(),
                sheet.groupId(),
                sheet.groupName(),
                sheet.targetEnvironment(),
                sheet.status(),
                sheet.author(),
                sheet.deliveryDate(),
                sheet.createdAt()
        );
    }
}
