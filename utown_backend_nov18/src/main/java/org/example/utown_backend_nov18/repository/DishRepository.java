package org.example.utown_backend_nov18.repository;

import org.example.utown_backend_nov18.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {
}
