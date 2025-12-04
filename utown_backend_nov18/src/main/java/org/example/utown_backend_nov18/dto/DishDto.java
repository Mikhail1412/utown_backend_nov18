package org.example.utown_backend_nov18.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Long restaurantId;
}
