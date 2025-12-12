package org.example.utown_backend_nov18.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.AddOrderItemRequest;
import org.example.utown_backend_nov18.dto.CreateOrderRequest;
import org.example.utown_backend_nov18.dto.OrderDto;
import org.example.utown_backend_nov18.dto.UpdateOrderItemRequest;
import org.example.utown_backend_nov18.dto.UpdateOrderStatusRequest;
import org.example.utown_backend_nov18.model.OrderStatus;
import org.example.utown_backend_nov18.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private boolean isAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @Operation(summary = "Create (or return existing) DRAFT order for current user and restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created (or existing draft returned)"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Restaurant/Table not found"),
            @ApiResponse(responseCode = "409", description = "Restaurant closed / table mismatch"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest req,
                                                Authentication auth) {
        log.info("POST /api/orders - create order");
        String email = auth.getName();
        OrderDto dto = orderService.createOrder(req, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Add item to DRAFT order (only owner can modify)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner)"),
            @ApiResponse(responseCode = "404", description = "Order/Dish not found"),
            @ApiResponse(responseCode = "409", description = "Only DRAFT orders can be modified / restaurant closed / dish mismatch"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<OrderDto> addItem(@PathVariable Long orderId,
                                            @Valid @RequestBody AddOrderItemRequest req,
                                            Authentication auth) {
        log.info("POST /api/orders/{}/items - add item", orderId);
        String email = auth.getName();
        OrderDto dto = orderService.addItem(orderId, req, email);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Update quantity of an item in DRAFT order (only owner can modify)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner)"),
            @ApiResponse(responseCode = "404", description = "Order/Item not found"),
            @ApiResponse(responseCode = "409", description = "Only DRAFT orders can be modified / restaurant closed"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> updateItem(@PathVariable Long orderId,
                                               @PathVariable Long itemId,
                                               @Valid @RequestBody UpdateOrderItemRequest req,
                                               Authentication auth) {
        log.info("PATCH /api/orders/{}/items/{} - update item", orderId, itemId);
        String email = auth.getName();
        OrderDto dto = orderService.updateItem(orderId, itemId, req, email);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Remove item from DRAFT order (only owner can modify)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner)"),
            @ApiResponse(responseCode = "404", description = "Order/Item not found"),
            @ApiResponse(responseCode = "409", description = "Only DRAFT orders can be modified / restaurant closed"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> removeItem(@PathVariable Long orderId,
                                               @PathVariable Long itemId,
                                               Authentication auth) {
        log.info("DELETE /api/orders/{}/items/{} - remove item", orderId, itemId);
        String email = auth.getName();
        OrderDto dto = orderService.removeItem(orderId, itemId, email);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Checkout DRAFT order (becomes PLACED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checked out"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner)"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Only DRAFT orders can be checked out / empty order / restaurant closed"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/orders/{orderId}/checkout")
    public ResponseEntity<OrderDto> checkout(@PathVariable Long orderId,
                                             Authentication auth) {
        log.info("POST /api/orders/{}/checkout", orderId);
        String email = auth.getName();
        OrderDto dto = orderService.checkout(orderId, email);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all my orders (no pagination)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/my/orders")
    public ResponseEntity<List<OrderDto>> getMyOrders(Authentication auth) {
        log.info("GET /api/my/orders");
        String email = auth.getName();
        List<OrderDto> list = orderService.getMyOrders(email);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get my orders (paged), optional filter by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders page"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/my/orders/paged")
    public ResponseEntity<Page<OrderDto>> getMyOrdersPaged(Pageable pageable,
                                                           @RequestParam(required = false) OrderStatus status,
                                                           Authentication auth) {
        log.info("GET /api/my/orders/paged status={}", status);
        String email = auth.getName();
        Page<OrderDto> page = orderService.getMyOrders(email, pageable, status);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get orders for a restaurant (owner/admin), no pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner/admin)"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/restaurants/{restaurantId}/orders")
    public ResponseEntity<List<OrderDto>> getOrdersForRestaurant(@PathVariable Long restaurantId,
                                                                 Authentication auth) {
        log.info("GET /api/restaurants/{}/orders", restaurantId);
        String email = auth.getName();
        boolean admin = isAdmin(auth);

        List<OrderDto> list = orderService.getOrdersForRestaurant(restaurantId, email, admin);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get orders for a restaurant (owner/admin), paged, optional filter by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders page"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (not owner/admin)"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/restaurants/{restaurantId}/orders/paged")
    public ResponseEntity<Page<OrderDto>> getOrdersForRestaurantPaged(@PathVariable Long restaurantId,
                                                                      Pageable pageable,
                                                                      @RequestParam(required = false) OrderStatus status,
                                                                      Authentication auth) {
        log.info("GET /api/restaurants/{}/orders/paged status={}", restaurantId, status);
        String email = auth.getName();
        boolean admin = isAdmin(auth);

        Page<OrderDto> page = orderService.getOrdersForRestaurant(restaurantId, email, admin, pageable, status);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Update order status (owner/admin; user can only cancel)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Invalid transition / restaurant closed"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long orderId,
                                                 @Valid @RequestBody UpdateOrderStatusRequest req,
                                                 Authentication auth) {
        log.info("PATCH /api/orders/{}/status -> {}", orderId, req.getStatus());
        String email = auth.getName();
        boolean admin = isAdmin(auth);

        OrderStatus status = req.getStatus();
        OrderDto dto = orderService.updateStatus(orderId, status, email, admin);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get order by id (order owner, restaurant owner or admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long orderId,
                                            Authentication auth) {
        log.info("GET /api/orders/{}", orderId);
        String email = auth.getName();
        boolean admin = isAdmin(auth);

        OrderDto dto = orderService.getById(orderId, email, admin);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Cancel order (maps to status=CANCELLED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Cannot be cancelled"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long orderId,
                                           Authentication auth) {
        log.info("DELETE /api/orders/{} - cancel", orderId);
        String email = auth.getName();
        boolean admin = isAdmin(auth);

        OrderDto dto = orderService.cancel(orderId, email, admin);
        return ResponseEntity.ok(dto);
    }
}
