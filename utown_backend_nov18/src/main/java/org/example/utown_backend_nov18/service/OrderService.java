package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.AddOrderItemRequest;
import org.example.utown_backend_nov18.dto.CreateOrderRequest;
import org.example.utown_backend_nov18.dto.OrderDto;
import org.example.utown_backend_nov18.dto.UpdateOrderItemRequest;
import org.example.utown_backend_nov18.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(CreateOrderRequest req, String userEmail);

    OrderDto addItem(Long orderId, AddOrderItemRequest req, String userEmail);

    OrderDto updateItem(Long orderId, Long itemId, UpdateOrderItemRequest req, String userEmail);

    OrderDto removeItem(Long orderId, Long itemId, String userEmail);

    OrderDto checkout(Long orderId, String userEmail);

    List<OrderDto> getMyOrders(String userEmail);

    Page<OrderDto> getMyOrders(String userEmail, Pageable pageable, OrderStatus status);

    List<OrderDto> getOrdersForRestaurant(Long restaurantId, String userEmail, boolean isAdmin);

    Page<OrderDto> getOrdersForRestaurant(Long restaurantId, String userEmail, boolean isAdmin,
                                          Pageable pageable, OrderStatus status);

    OrderDto getById(Long id, String userEmail, boolean isAdmin);

    OrderDto updateStatus(Long orderId, OrderStatus status, String userEmail, boolean isAdmin);

    OrderDto cancel(Long orderId, String userEmail, boolean isAdmin);

    OrderDto getMyCart(Long restaurantId, String userEmail);
}
