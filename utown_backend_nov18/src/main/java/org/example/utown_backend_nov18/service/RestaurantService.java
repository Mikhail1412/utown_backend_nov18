package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateRestaurantRequest;
import org.example.utown_backend_nov18.dto.UpdateRestaurantRequest;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.model.RestaurantStatus;

import java.util.List;

public interface RestaurantService {

    List<Restaurant> findAll();

    Restaurant findByIdOrThrow(Long id);

    Restaurant createRestaurant(CreateRestaurantRequest req, String currentUserEmail);

    Restaurant updateRestaurant(Long id, UpdateRestaurantRequest req, String currentUserEmail, boolean isAdmin);

    void deleteRestaurant(Long id, String currentUserEmail, boolean isAdmin);

    Restaurant updateStatus(Long restaurantId, RestaurantStatus status, String currentUserEmail, boolean isAdmin);
}
