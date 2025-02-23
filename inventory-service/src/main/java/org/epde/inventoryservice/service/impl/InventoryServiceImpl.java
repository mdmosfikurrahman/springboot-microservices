package org.epde.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.epde.inventoryservice.repository.InventoryRepository;
import org.epde.inventoryservice.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }

}
