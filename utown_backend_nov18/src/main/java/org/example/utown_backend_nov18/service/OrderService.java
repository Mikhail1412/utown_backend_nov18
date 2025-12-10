package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.AddOrderItemRequest;
import org.example.utown_backend_nov18.dto.CreateOrderRequest;
import org.example.utown_backend_nov18.dto.OrderDto;
import org.example.utown_backend_nov18.model.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDto createOrder(CreateOrderRequest req);

    OrderDto addItem(Long orderId, AddOrderItemRequest req);

    OrderDto updateItem(Long orderId, Long itemId, AddOrderItemRequest req);

    OrderDto removeItem(Long orderId, Long itemId);

    OrderDto checkout(Long orderId);

    List<OrderDto> getMyOrders();

    List<OrderDto> getOrdersForRestaurant(Long restaurantId);

    Optional<OrderDto> getById(Long id);

    OrderDto updateStatus(Long orderId, OrderStatus status);
}
