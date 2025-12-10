package org.example.utown_backend_nov18.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Long tableId;
    private String status;
    private Integer totalPrice;
    private List<OrderItemDto> items;
}
