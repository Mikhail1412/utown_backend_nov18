package org.example.utown_backend_nov18.dto;

import lombok.Data;

@Data
public class DiningAreaDto {
    private Long id;
    private String name;
    private String description;
    private Long restaurantId;
}
