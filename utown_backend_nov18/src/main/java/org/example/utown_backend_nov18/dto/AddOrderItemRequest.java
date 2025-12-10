package org.example.utown_backend_nov18.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddOrderItemRequest {

    @NotNull
    private Long dishId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
