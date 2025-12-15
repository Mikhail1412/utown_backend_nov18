package org.example.utown_backend_nov18.controller;

import lombok.RequiredArgsConstructor;
import org.example.utown_backend_nov18.dto.AddOrderItemRequest;
import org.example.utown_backend_nov18.dto.CreateOrderRequest;
import org.example.utown_backend_nov18.dto.OrderDto;
import org.example.utown_backend_nov18.dto.UpdateOrderItemRequest;
import org.example.utown_backend_nov18.dto.UpdateOrderStatusRequest;
import org.example.utown_backend_nov18.model.OrderStatus;
import org.example.utown_backend_nov18.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderRequest req, Authentication auth) {
        return ResponseEntity.status(201).body(orderService.createOrder(req, auth.getName()));
    }

    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<OrderDto> addItem(@PathVariable Long orderId,
                                            @RequestBody AddOrderItemRequest req,
                                            Authentication auth) {
        return ResponseEntity.ok(orderService.addItem(orderId, req, auth.getName()));
    }

    @PatchMapping("/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> updateItem(@PathVariable Long orderId,
                                               @PathVariable Long itemId,
                                               @RequestBody UpdateOrderItemRequest req,
                                               Authentication auth) {
        return ResponseEntity.ok(orderService.updateItem(orderId, itemId, req, auth.getName()));
    }

    @DeleteMapping("/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> removeItem(@PathVariable Long orderId,
                                               @PathVariable Long itemId,
                                               Authentication auth) {
        return ResponseEntity.ok(orderService.removeItem(orderId, itemId, auth.getName()));
    }

    @PostMapping("/orders/{orderId}/checkout")
    public ResponseEntity<OrderDto> checkout(@PathVariable Long orderId, Authentication auth) {
        return ResponseEntity.ok(orderService.checkout(orderId, auth.getName()));
    }

    @GetMapping("/my/orders")
    public ResponseEntity<List<OrderDto>> myOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getMyOrders(auth.getName()));
    }

    @GetMapping("/my/orders/page")
    public ResponseEntity<Page<OrderDto>> myOrdersPage(Authentication auth,
                                                       Pageable pageable,
                                                       @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getMyOrders(auth.getName(), pageable, status));
    }

    @GetMapping("/my/cart")
    public ResponseEntity<List<OrderDto>> myCart(Authentication auth) {
        return ResponseEntity.ok(orderService.getMyCart(auth.getName()));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = false;
        return ResponseEntity.ok(orderService.getById(id, auth.getName(), isAdmin));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long orderId,
                                                 @RequestBody UpdateOrderStatusRequest req,
                                                 Authentication auth) {
        boolean isAdmin = false;
        return ResponseEntity.ok(orderService.updateStatus(orderId, req.getStatus(), auth.getName(), isAdmin));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long orderId, Authentication auth) {
        boolean isAdmin = false;
        return ResponseEntity.ok(orderService.cancel(orderId, auth.getName(), isAdmin));
    }
}
