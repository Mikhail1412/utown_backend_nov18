package org.example.utown_backend_nov18.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String type;
    private Instant timestamp;

    private Long orderId;
    private String orderStatus;

    private Long restaurantId;
    private String restaurantStatus;

    private String message;

    public static NotificationDto now(String type,
                                      Long orderId,
                                      String orderStatus,
                                      Long restaurantId,
                                      String restaurantStatus,
                                      String message) {
        return new NotificationDto(
                type,
                Instant.now(),
                orderId,
                orderStatus,
                restaurantId,
                restaurantStatus,
                message
        );
    }
}
