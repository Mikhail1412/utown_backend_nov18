package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.NotificationDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUser(Long userId, NotificationDto payload) {
        messagingTemplate.convertAndSend("/topic/users/" + userId, payload);
    }

    public void notifyRestaurant(Long restaurantId, NotificationDto payload) {
        messagingTemplate.convertAndSend("/topic/restaurants/" + restaurantId, payload);
    }

    public void notifyOrder(Long orderId, NotificationDto payload) {
        messagingTemplate.convertAndSend("/topic/orders/" + orderId, payload);
    }
}
