package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateRestaurantRequest;
import org.example.utown_backend_nov18.dto.UpdateRestaurantRequest;
import org.example.utown_backend_nov18.exception.AccessDeniedDomainException;
import org.example.utown_backend_nov18.exception.NotFoundException;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.model.RestaurantStatus;
import org.example.utown_backend_nov18.model.User;
import org.example.utown_backend_nov18.repository.RestaurantRepository;
import org.example.utown_backend_nov18.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurants;
    private final UserRepository users;

    public RestaurantService(RestaurantRepository restaurants, UserRepository users) {
        this.restaurants = restaurants;
        this.users = users;
    }

    public List<Restaurant> findAll() {
        return restaurants.findAll();
    }

    public Restaurant findByIdOrThrow(Long id) {
        return restaurants.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + id));
    }

    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, String currentUserEmail) {
        User owner = users.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("User not found: " + currentUserEmail));

        Restaurant r = new Restaurant();
        r.setName(safeTrim(req.getName()));
        r.setAddress(safeTrim(req.getAddress()));
        r.setPhoneNumber(safeTrim(req.getPhoneNumber()));
        r.setDescription(safeTrim(req.getDescription()));
        r.setOwner(owner);

        return restaurants.save(r);
    }

    @Transactional
    public Restaurant updateRestaurant(Long id, UpdateRestaurantRequest req, String currentUserEmail, boolean isAdmin) {
        Restaurant existing = findByIdOrThrow(id);
        assertOwnerOrAdmin(existing, currentUserEmail, isAdmin);

        existing.setName(safeTrim(req.getName()));
        existing.setAddress(safeTrim(req.getAddress()));
        existing.setPhoneNumber(safeTrim(req.getPhoneNumber()));
        existing.setDescription(safeTrim(req.getDescription()));

        return restaurants.save(existing);
    }

    @Transactional
    public void deleteRestaurant(Long id, String currentUserEmail, boolean isAdmin) {
        Restaurant existing = findByIdOrThrow(id);
        assertOwnerOrAdmin(existing, currentUserEmail, isAdmin);
        restaurants.delete(existing);
    }

    @Transactional
    public Restaurant updateStatus(Long restaurantId, RestaurantStatus status, String currentUserEmail, boolean isAdmin) {
        Restaurant restaurant = findByIdOrThrow(restaurantId);
        assertOwnerOrAdmin(restaurant, currentUserEmail, isAdmin);

        restaurant.setStatus(status);
        return restaurants.save(restaurant);
    }

    private void assertOwnerOrAdmin(Restaurant r, String currentUserEmail, boolean isAdmin) {
        if (isAdmin) return;
        if (r.getOwner() == null || r.getOwner().getEmail() == null) {
            throw new AccessDeniedDomainException("Restaurant owner is not set");
        }
        if (!r.getOwner().getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new AccessDeniedDomainException("Only owner/admin can modify this restaurant");
        }
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
