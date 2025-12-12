package org.example.utown_backend_nov18.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Long tableId;
    private String status;
    private BigDecimal totalPrice;
    private List<OrderItemDto> items;
}
