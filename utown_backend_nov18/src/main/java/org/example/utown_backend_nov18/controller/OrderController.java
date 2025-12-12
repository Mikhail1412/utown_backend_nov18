package org.example.utown_backend_nov18.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.AddOrderItemRequest;
import org.example.utown_backend_nov18.dto.CreateOrderRequest;
import org.example.utown_backend_nov18.dto.OrderDto;
import org.example.utown_backend_nov18.dto.UpdateOrderItemRequest;
import org.example.utown_backend_nov18.dto.UpdateOrderStatusRequest;
import org.example.utown_backend_nov18.model.OrderStatus;
import org.example.utown_backend_nov18.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest req) {
        log.info("POST /api/orders - create order");
        OrderDto dto = orderService.createOrder(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<OrderDto> addItem(@PathVariable Long orderId,
                                            @Valid @RequestBody AddOrderItemRequest req) {
        log.info("POST /api/orders/{}/items - add item", orderId);
        OrderDto dto = orderService.addItem(orderId, req);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> updateItem(@PathVariable Long orderId,
                                               @PathVariable Long itemId,
                                               @Valid @RequestBody UpdateOrderItemRequest req) {
        log.info("PATCH /api/orders/{}/items/{} - update item", orderId, itemId);
        OrderDto dto = orderService.updateItem(orderId, itemId, req);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> removeItem(@PathVariable Long orderId,
                                               @PathVariable Long itemId) {
        log.info("DELETE /api/orders/{}/items/{} - remove item", orderId, itemId);
        OrderDto dto = orderService.removeItem(orderId, itemId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/orders/{orderId}/checkout")
    public ResponseEntity<OrderDto> checkout(@PathVariable Long orderId) {
        log.info("POST /api/orders/{}/checkout", orderId);
        OrderDto dto = orderService.checkout(orderId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my/orders")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        log.info("GET /api/my/orders");
        List<OrderDto> list = orderService.getMyOrders();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/restaurants/{restaurantId}/orders")
    public ResponseEntity<List<OrderDto>> getOrdersForRestaurant(@PathVariable Long restaurantId) {
        log.info("GET /api/restaurants/{}/orders", restaurantId);
        List<OrderDto> list = orderService.getOrdersForRestaurant(restaurantId);
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long orderId,
                                                 @Valid @RequestBody UpdateOrderStatusRequest req) {
        log.info("PATCH /api/orders/{}/status -> {}", orderId, req.getStatus());
        OrderStatus status = req.getStatus();
        OrderDto dto = orderService.updateStatus(orderId, status);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long orderId) {
        log.info("GET /api/orders/{}", orderId);
        Optional<OrderDto> opt = orderService.getById(orderId);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
