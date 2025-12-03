package org.example.utown_backend_nov18.repository;

import org.example.utown_backend_nov18.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
