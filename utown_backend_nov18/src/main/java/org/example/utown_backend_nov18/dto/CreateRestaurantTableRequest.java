package org.example.utown_backend_nov18.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRestaurantTableRequest {

    @NotBlank
    private String tableNumber;

    @Min(1)
    private int capacity;

    @NotNull
    private Long diningAreaId;
}
