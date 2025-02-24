package org.epde.orderservice.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String orderNumber;
    private String message;
    private List<String> outOfStockSkuCodes;
}
