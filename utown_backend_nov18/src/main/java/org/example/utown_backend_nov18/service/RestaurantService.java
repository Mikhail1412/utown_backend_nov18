package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.model.Restaurant;
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

    public Restaurant save(Restaurant restaurant) {
        return repo.save(restaurant);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
