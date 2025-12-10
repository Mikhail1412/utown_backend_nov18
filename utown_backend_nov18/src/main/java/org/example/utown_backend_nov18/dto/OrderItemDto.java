package org.example.utown_backend_nov18.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private Integer priceAtMoment;
}
