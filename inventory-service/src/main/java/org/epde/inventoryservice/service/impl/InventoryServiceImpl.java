package org.epde.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.epde.inventoryservice.dto.InventoryResponse;
import org.epde.inventoryservice.repository.InventoryRepository;
import org.epde.inventoryservice.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                )
                .toList();
    }

}
