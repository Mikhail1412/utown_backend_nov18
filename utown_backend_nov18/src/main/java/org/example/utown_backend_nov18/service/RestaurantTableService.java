package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.model.RestaurantTable;
import org.example.utown_backend_nov18.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository repo;

    public RestaurantTableService(RestaurantTableRepository repo) {
        this.repo = repo;
    }

    public List<RestaurantTable> findAll() {
        return repo.findAll();
    }

    public Optional<RestaurantTable> findById(Long id) {
        return repo.findById(id);
    }

    public RestaurantTable save(RestaurantTable table) {
        return repo.save(table);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
