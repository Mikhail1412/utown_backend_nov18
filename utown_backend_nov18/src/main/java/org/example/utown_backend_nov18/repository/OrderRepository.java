package org.example.utown_backend_nov18.repository;

import org.example.utown_backend_nov18.model.Order;
import org.example.utown_backend_nov18.model.OrderStatus;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByRestaurant(Restaurant restaurant);

    Optional<Order> findByUserAndRestaurantAndStatus(User user,
                                                     Restaurant restaurant,
                                                     OrderStatus status);

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByRestaurant(Restaurant restaurant, Pageable pageable);

    Optional<Order> findByIdAndUser(Long id, User user);

    Optional<Order> findByIdAndRestaurant(Long id, Restaurant restaurant);

    Page<Order> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);

    Page<Order> findByRestaurantAndStatus(Restaurant restaurant, OrderStatus status, Pageable pageable);
}
