package org.example.utown_backend_nov18.service;

import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.AddOrderItemRequest;
import org.example.utown_backend_nov18.dto.CreateOrderRequest;
import org.example.utown_backend_nov18.dto.OrderDto;
import org.example.utown_backend_nov18.dto.OrderItemDto;
import org.example.utown_backend_nov18.dto.UpdateOrderItemRequest;
import org.example.utown_backend_nov18.exception.AccessDeniedDomainException;
import org.example.utown_backend_nov18.exception.BusinessConflictException;
import org.example.utown_backend_nov18.exception.EmptyOrderException;
import org.example.utown_backend_nov18.exception.NotFoundException;
import org.example.utown_backend_nov18.model.*;
import org.example.utown_backend_nov18.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final DishRepository dishRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            RestaurantRepository restaurantRepository,
                            RestaurantTableRepository restaurantTableRepository,
                            DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.dishRepository = dishRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Current user not found by email: " + email));
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest req) {
        User currentUser = getCurrentUser();

        Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + req.getRestaurantId()));

        if (restaurant.getStatus() == RestaurantStatus.CLOSED) {
            throw new BusinessConflictException("Restaurant is closed for orders");
        }

        RestaurantTable table = null;
        if (req.getTableId() != null) {
            table = restaurantTableRepository.findById(req.getTableId())
                    .orElseThrow(() -> new NotFoundException("Table not found: " + req.getTableId()));

            if (table.getDiningArea() == null
                    || table.getDiningArea().getRestaurant() == null
                    || !table.getDiningArea().getRestaurant().getId().equals(restaurant.getId())) {
                throw new BusinessConflictException("Table does not belong to this restaurant");
            }
        }

        Optional<Order> existingDraftOpt =
                orderRepository.findByUserAndRestaurantAndStatus(currentUser, restaurant, OrderStatus.DRAFT);

        if (existingDraftOpt.isPresent()) {
            Order existing = existingDraftOpt.get();
            if (table != null) {
                existing.setTable(table);
            }

            log.info("Reusing existing DRAFT order {} for user {} and restaurant {}",
                    existing.getId(), currentUser.getEmail(), restaurant.getId());

            Order saved = orderRepository.save(existing);
            return toDto(saved);
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setRestaurant(restaurant);
        order.setTable(table);
        order.setStatus(OrderStatus.DRAFT);
        order.setTotalPrice(BigDecimal.ZERO);

        Order saved = orderRepository.save(order);
        log.info("Order created with id {} by user {}", saved.getId(), currentUser.getEmail());

        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto addItem(Long orderId, AddOrderItemRequest req) {
        User currentUser = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedDomainException("You can modify only your own orders");
        }
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessConflictException("Only DRAFT orders can be modified");
        }
        if (order.getRestaurant().getStatus() == RestaurantStatus.CLOSED) {
            throw new BusinessConflictException("Restaurant is closed for orders");
        }

        Dish dish = dishRepository.findById(req.getDishId())
                .orElseThrow(() -> new NotFoundException("Dish not found: " + req.getDishId()));

        if (!dish.getRestaurant().getId().equals(order.getRestaurant().getId())) {
            throw new BusinessConflictException("Dish does not belong to this restaurant");
        }

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setDish(dish);
        item.setQuantity(req.getQuantity());
        item.setPriceAtMoment(dish.getPrice());

        order.getItems().add(item);

        recalcTotal(order);

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto updateItem(Long orderId, Long itemId, UpdateOrderItemRequest req) {
        User currentUser = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedDomainException("You can modify only your own orders");
        }
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessConflictException("Only DRAFT orders can be modified");
        }
        if (order.getRestaurant().getStatus() == RestaurantStatus.CLOSED) {
            throw new BusinessConflictException("Restaurant is closed for orders");
        }

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Order item not found: " + itemId));

        item.setQuantity(req.getQuantity());

        recalcTotal(order);

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto removeItem(Long orderId, Long itemId) {
        User currentUser = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedDomainException("You can modify only your own orders");
        }
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessConflictException("Only DRAFT orders can be modified");
        }
        if (order.getRestaurant().getStatus() == RestaurantStatus.CLOSED) {
            throw new BusinessConflictException("Restaurant is closed for orders");
        }

        Set<OrderItem> items = order.getItems();
        OrderItem toRemove = items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Order item not found: " + itemId));

        items.remove(toRemove);
        orderItemRepository.delete(toRemove);

        recalcTotal(order);

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto checkout(Long orderId) {
        User currentUser = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedDomainException("You can modify only your own orders");
        }
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessConflictException("Only DRAFT orders can be checked out");
        }

        if (order.getItems().isEmpty()) {
            throw new EmptyOrderException("Cannot checkout empty order");
        }

        if (order.getRestaurant().getStatus() == RestaurantStatus.CLOSED) {
            throw new BusinessConflictException("Restaurant is closed for orders");
        }

        order.setStatus(OrderStatus.PLACED);

        Order saved = orderRepository.save(order);
        log.info("Order {} checked out by user {}", saved.getId(), currentUser.getEmail());

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getMyOrders() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderRepository.findByUser(currentUser);
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + restaurantId));

        List<Order> orders = orderRepository.findByRestaurant(restaurant);
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getById(Long id) {
        User currentUser = getCurrentUser();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        if (!isAdmin() && !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedDomainException("You can view only your own orders");
        }

        return Optional.of(toDto(order));
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long orderId, OrderStatus status) {
        User currentUser = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!isAdmin()) {

            if (!order.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedDomainException("You can modify only your own orders");
            }

            if (status != OrderStatus.CANCELLED) {
                throw new AccessDeniedDomainException("Only ADMIN can set this status");
            }

            if (order.getStatus() != OrderStatus.DRAFT && order.getStatus() != OrderStatus.PLACED) {
                throw new BusinessConflictException("This order cannot be cancelled");
            }
        }

        order.setStatus(status);

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    private void recalcTotal(Order order) {
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getPriceAtMoment().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(total);
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setRestaurantId(order.getRestaurant().getId());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setTableId(order.getTable() != null ? order.getTable().getId() : null);
        dto.setStatus(order.getStatus().name());
        dto.setTotalPrice(order.getTotalPrice());

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    OrderItemDto d = new OrderItemDto();
                    d.setId(item.getId());
                    d.setDishId(item.getDish().getId());
                    d.setDishName(item.getDish().getName());
                    d.setQuantity(item.getQuantity());
                    d.setPriceAtMoment(item.getPriceAtMoment());
                    return d;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}
