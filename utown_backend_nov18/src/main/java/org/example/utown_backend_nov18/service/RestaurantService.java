package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateRestaurantRequest;
import org.example.utown_backend_nov18.dto.UpdateRestaurantRequest;
import org.example.utown_backend_nov18.exception.NotFoundException;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.model.RestaurantStatus;
import org.example.utown_backend_nov18.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository repo;

    public RestaurantService(RestaurantRepository repo) {
        this.repo = repo;
    }

    public List<Restaurant> findAll() {
        return repo.findAll();
    }

    public Optional<Restaurant> findById(Long id) {
        return repo.findById(id);
    }

    public Restaurant createRestaurant(CreateRestaurantRequest req) {
        Restaurant r = new Restaurant();
        r.setName(safeTrim(req.getName()));
        r.setAddress(safeTrim(req.getAddress()));
        r.setPhoneNumber(safeTrim(req.getPhoneNumber()));
        r.setDescription(safeTrim(req.getDescription()));
        return repo.save(r);
    }

    public Optional<Restaurant> updateRestaurant(Long id, UpdateRestaurantRequest req) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setName(safeTrim(req.getName()));
                    existing.setAddress(safeTrim(req.getAddress()));
                    existing.setPhoneNumber(safeTrim(req.getPhoneNumber()));
                    existing.setDescription(safeTrim(req.getDescription()));
                    return repo.save(existing);
                });
    }

    public boolean deleteRestaurant(Long id) {
        if (!repo.existsById(id)) {
            return false;
        }
        repo.deleteById(id);
        return true;
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    public Restaurant updateStatus(Long restaurantId, RestaurantStatus status) {
        return repo.findById(restaurantId)
                .map(restaurant -> {
                    restaurant.setStatus(status);
                    return repo.save(restaurant);
                })
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + restaurantId));
    }
}