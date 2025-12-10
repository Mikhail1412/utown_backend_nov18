package org.example.utown_backend_nov18.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.utown_backend_nov18.model.OrderStatus;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
