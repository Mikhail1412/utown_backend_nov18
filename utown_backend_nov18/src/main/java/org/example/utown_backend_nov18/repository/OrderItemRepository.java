package org.example.utown_backend_nov18.repository;

import org.example.utown_backend_nov18.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
