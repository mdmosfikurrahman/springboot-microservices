package org.epde.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.epde.orderservice.dto.InventoryResponse;
import org.epde.orderservice.dto.OrderLineItemsRequest;
import org.epde.orderservice.dto.OrderRequest;
import org.epde.orderservice.entity.Order;
import org.epde.orderservice.entity.OrderLineItems;
import org.epde.orderservice.repository.OrderRepository;
import org.epde.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final WebClient webClient;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(convertToEntities(orderRequest))
                .build();

        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if (Boolean.TRUE.equals(allProductsInStock)) {
            repository.save(order);
        } else {
            throw new IllegalArgumentException("Order is not in stock");
        }

        log.info("Order Placed: {}", order);
    }

    private List<OrderLineItems> convertToEntities(OrderRequest orderRequest) {
        return orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapToEntity)
                .toList();
    }

    private OrderLineItems mapToEntity(OrderLineItemsRequest orderLineItem) {
        return OrderLineItems.builder()
                .skuCode(orderLineItem.getSkuCode())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }
}
