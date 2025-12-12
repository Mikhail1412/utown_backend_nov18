package org.example.utown_backend_nov18.dto;

import lombok.Data;

@Data
public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String status;
    private Long ownerId;
}
