package org.epde.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.epde.orderservice.dto.OrderRequest;
import org.epde.orderservice.dto.OrderResponse;
import org.epde.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

}
