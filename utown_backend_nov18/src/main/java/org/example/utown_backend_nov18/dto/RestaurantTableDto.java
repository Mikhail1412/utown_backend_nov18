package org.example.utown_backend_nov18.dto;

import lombok.Data;

@Data
public class RestaurantTableDto {
    private Long id;
    private String tableNumber;
    private int capacity;
    private boolean active;
    private Long diningAreaId;
}
