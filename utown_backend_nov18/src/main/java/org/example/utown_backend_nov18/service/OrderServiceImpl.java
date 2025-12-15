package org.example.utown_backend_nov18.service;

import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.AddOrderItemRequest;
import org.example.utown_backend_nov18.dto.CreateOrderRequest;
import org.example.utown_backend_nov18.dto.NotificationDto;
import org.example.utown_backend_nov18.dto.OrderDto;
import org.example.utown_backend_nov18.dto.OrderItemDto;
import org.example.utown_backend_nov18.dto.UpdateOrderItemRequest;
import org.example.utown_backend_nov18.exception.AccessDeniedDomainException;
import org.example.utown_backend_nov18.exception.BusinessConflictException;
import org.example.utown_backend_nov18.exception.EmptyOrderException;
import org.example.utown_backend_nov18.exception.NotFoundException;
import org.example.utown_backend_nov18.model.*;
import org.example.utown_backend_nov18.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final NotificationService notificationService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            RestaurantRepository restaurantRepository,
                            RestaurantTableRepository restaurantTableRepository,
                            DishRepository dishRepository,
                            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.dishRepository = dishRepository;
        this.notificationService = notificationService;
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found by email: " + email));
    }

    private void assertOrderOwner(Order order, User user) {
        if (order.getUser() == null || !order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedDomainException("You can modify only your own orders");
        }
    }

    private boolean isRestaurantOwner(Restaurant restaurant, String userEmail) {
        if (restaurant == null || restaurant.getOwner() == null || restaurant.getOwner().getEmail() == null) {
            return false;
        }
        return restaurant.getOwner().getEmail().equalsIgnoreCase(userEmail);
    }

    private void assertRestaurantOwnerOrAdmin(Restaurant restaurant, String userEmail, boolean isAdmin) {
        if (isAdmin) return;
        if (!isRestaurantOwner(restaurant, userEmail)) {
            throw new AccessDeniedDomainException("Only restaurant owner/admin can access restaurant orders");
        }
    }

    private void assertCanViewOrder(Order order, String userEmail, boolean isAdmin) {
        if (isAdmin) return;

        if (order.getUser() != null && order.getUser().getEmail() != null
                && order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            return;
        }

        if (isRestaurantOwner(order.getRestaurant(), userEmail)) {
            return;
        }

        throw new AccessDeniedDomainException("You can view only your own orders");
    }

    private void assertStatusTransitionAllowed(OrderStatus from, OrderStatus to) {
        if (from == to) return;

        if (to == OrderStatus.CANCELLED) {
            if (from != OrderStatus.DRAFT && from != OrderStatus.PLACED) {
                throw new BusinessConflictException("This order cannot be cancelled");
            }
            return;
        }

        switch (from) {
            case DRAFT -> {
                if (to != OrderStatus.PLACED) {
                    throw new BusinessConflictException("Invalid status transition: " + from + " -> " + to);
                }
            }
            case PLACED -> {
                if (to != OrderStatus.IN_PROGRESS) {
                    throw new BusinessConflictException("Invalid status transition: " + from + " -> " + to);
                }
            }
            case IN_PROGRESS -> {
                if (to != OrderStatus.DONE) {
                    throw new BusinessConflictException("Invalid status transition: " + from + " -> " + to);
                }
            }
            case DONE, CANCELLED -> throw new BusinessConflictException("This order cannot change status anymore");
        }
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest req, String userEmail) {
        User currentUser = getUserByEmailOrThrow(userEmail);

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
        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto addItem(Long orderId, AddOrderItemRequest req, String userEmail) {
        User currentUser = getUserByEmailOrThrow(userEmail);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        assertOrderOwner(order, currentUser);

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessConflictException("Only DRAFT orders can be modified");
        }
        if (order.getRestaurant().getStatus() == RestaurantStatus.CLOSED) {
            throw new BusinessConflictException("Restaurant is closed for orders");
        }

        Dish dish = dishRepository.findById(req.getDishId())
                .orElseThrow(() -> new NotFoundException("Dish not found: " + req.getDishId()));

        if (dish.getRestaurant() == null || !dish.getRestaurant().getId().equals(order.getRestaurant().getId())) {
            throw new BusinessConflictException("Dish does not belong to this restaurant");
        }

        if (!dish.isActive()) {
            throw new BusinessConflictException("Dish is not active");
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
    public OrderDto updateItem(Long orderId, Long itemId, UpdateOrderItemRequest req, String userEmail) {
        User currentUser = getUserByEmailOrThrow(userEmail);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        assertOrderOwner(order, currentUser);

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
    public OrderDto removeItem(Long orderId, Long itemId, String userEmail) {
        User currentUser = getUserByEmailOrThrow(userEmail);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        assertOrderOwner(order, currentUser);

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
    public OrderDto checkout(Long orderId, String userEmail) {
        User currentUser = getUserByEmailOrThrow(userEmail);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        assertOrderOwner(order, currentUser);

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

        notificationService.notifyUser(
                currentUser.getId(),
                NotificationDto.now(
                        "ORDER_PLACED",
                        order.getId(),
                        order.getStatus().name(),
                        order.getRestaurant().getId(),
                        order.getRestaurant().getStatus().name(),
                        "Order placed"
                )
        );

        notificationService.notifyRestaurant(
                order.getRestaurant().getId(),
                NotificationDto.now(
                        "ORDER_PLACED",
                        order.getId(),
                        order.getStatus().name(),
                        order.getRestaurant().getId(),
                        order.getRestaurant().getStatus().name(),
                        "New order placed"
                )
        );

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getMyOrders(String userEmail) {
        User currentUser = getUserByEmailOrThrow(userEmail);
        return orderRepository.findByUser(currentUser).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getMyOrders(String userEmail, Pageable pageable, OrderStatus status) {
        User currentUser = getUserByEmailOrThrow(userEmail);

        if (status == null) {
            return orderRepository.findByUser(currentUser, pageable).map(this::toDto);
        }

        return orderRepository.findByUserAndStatus(currentUser, status, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getMyCart(String userEmail) {
        User currentUser = getUserByEmailOrThrow(userEmail);
        return orderRepository.findByUserAndStatus(currentUser, OrderStatus.DRAFT).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersForRestaurant(Long restaurantId, String userEmail, boolean isAdmin) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + restaurantId));

        assertRestaurantOwnerOrAdmin(restaurant, userEmail, isAdmin);

        return orderRepository.findByRestaurant(restaurant).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersForRestaurant(Long restaurantId, String userEmail, boolean isAdmin,
                                                 Pageable pageable, OrderStatus status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + restaurantId));

        assertRestaurantOwnerOrAdmin(restaurant, userEmail, isAdmin);

        if (status == null) {
            return orderRepository.findByRestaurant(restaurant, pageable).map(this::toDto);
        }

        return orderRepository.findByRestaurantAndStatus(restaurant, status, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getById(Long id, String userEmail, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        assertCanViewOrder(order, userEmail, isAdmin);

        return toDto(order);
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long orderId, OrderStatus status, String userEmail, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        boolean restaurantOwner = isRestaurantOwner(order.getRestaurant(), userEmail);
        boolean orderOwner = order.getUser() != null
                && order.getUser().getEmail() != null
                && order.getUser().getEmail().equalsIgnoreCase(userEmail);

        if (order.getRestaurant().getStatus() == RestaurantStatus.CLOSED && status != OrderStatus.CANCELLED) {
            throw new BusinessConflictException("Restaurant is closed for orders");
        }

        if (!isAdmin && !restaurantOwner) {
            if (!orderOwner) {
                throw new AccessDeniedDomainException("You can modify only your own orders");
            }
            if (status != OrderStatus.CANCELLED) {
                throw new AccessDeniedDomainException("Only restaurant owner/admin can set this status");
            }
        }

        OrderStatus from = order.getStatus();
        assertStatusTransitionAllowed(from, status);

        if (from == status) {
            return toDto(order);
        }

        order.setStatus(status);

        if (order.getUser() != null) {
            notificationService.notifyUser(
                    order.getUser().getId(),
                    NotificationDto.now(
                            "ORDER_STATUS_CHANGED",
                            order.getId(),
                            order.getStatus().name(),
                            order.getRestaurant().getId(),
                            order.getRestaurant().getStatus().name(),
                            "Order status changed: " + from + " -> " + status
                    )
            );
        }

        notificationService.notifyRestaurant(
                order.getRestaurant().getId(),
                NotificationDto.now(
                        "ORDER_STATUS_CHANGED",
                        order.getId(),
                        order.getStatus().name(),
                        order.getRestaurant().getId(),
                        order.getRestaurant().getStatus().name(),
                        "Order status changed: " + from + " -> " + status
                )
        );

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto cancel(Long orderId, String userEmail, boolean isAdmin) {
        return updateStatus(orderId, OrderStatus.CANCELLED, userEmail, isAdmin);
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
