package org.example.utown_backend_nov18.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderItemRequest {

    @NotNull
    @Min(1)
    private Integer quantity;
}
