package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.model.Dish;
import org.example.utown_backend_nov18.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    private final DishRepository repo;

    public DishService(DishRepository repo) {
        this.repo = repo;
    }

    public List<Dish> findAll() {
        return repo.findAll();
    }

    public Optional<Dish> findById(Long id) {
        return repo.findById(id);
    }

    public Dish save(Dish dish) {
        return repo.save(dish);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
