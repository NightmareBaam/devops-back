package com.devops.domain.port;

import com.devops.domain.model.DeliverySheet;

import java.util.List;
import java.util.Optional;

public interface DeliverySheetPort {

    List<DeliverySheet> findAll();

    Optional<DeliverySheet> findById(String id);

    DeliverySheet save(DeliverySheet sheet);
}
