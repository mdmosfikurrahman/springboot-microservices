package org.epde.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.epde.orderservice.dto.InventoryResponse;
import org.epde.orderservice.dto.OrderLineItemsRequest;
import org.epde.orderservice.dto.OrderRequest;
import org.epde.orderservice.dto.OrderResponse;
import org.epde.orderservice.entity.Order;
import org.epde.orderservice.entity.OrderLineItems;
import org.epde.orderservice.repository.OrderRepository;
import org.epde.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final WebClient webClient;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(convertToEntities(orderRequest))
                .build();

        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/api/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if (inventoryResponses == null || inventoryResponses.length == 0) {
            throw new IllegalStateException("No inventory data available for the requested SKU codes");
        }

        Map<String, Boolean> inventoryMap = Arrays.stream(inventoryResponses)
                .collect(Collectors.toMap(InventoryResponse::getSkuCode, InventoryResponse::isInStock));

        List<OrderLineItems> inStockItems = order.getOrderLineItems()
                .stream()
                .filter(item -> inventoryMap.getOrDefault(item.getSkuCode(), false))
                .toList();

        List<String> outOfStockSkuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .filter(skuCode -> !inventoryMap.getOrDefault(skuCode, false))
                .toList();

        if (inStockItems.isEmpty()) {
            throw new IllegalArgumentException("None of the items are in stock. Out of stock items: " + outOfStockSkuCodes);
        }

        order.setOrderLineItems(inStockItems);
        repository.save(order);
        log.info("Order Placed: {}", order);

        String message = outOfStockSkuCodes.isEmpty()
                ? "Order placed successfully"
                : "Partial order placed. The following items were out of stock: " + outOfStockSkuCodes;

        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .outOfStockSkuCodes(outOfStockSkuCodes)
                .message(message)
                .build();
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
