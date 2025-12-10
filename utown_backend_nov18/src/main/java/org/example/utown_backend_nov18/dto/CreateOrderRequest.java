package org.example.utown_backend_nov18.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long restaurantId;

    private Long tableId;
}
