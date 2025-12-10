package org.example.utown_backend_nov18.repository;

import org.example.utown_backend_nov18.model.Order;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByRestaurant(Restaurant restaurant);
}
