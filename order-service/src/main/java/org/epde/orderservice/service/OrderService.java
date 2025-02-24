package org.epde.orderservice.service;

import org.epde.orderservice.dto.OrderRequest;
import org.epde.orderservice.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest);
}
