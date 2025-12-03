package org.example.utown_backend_nov18.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRestaurantRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String description;
}
