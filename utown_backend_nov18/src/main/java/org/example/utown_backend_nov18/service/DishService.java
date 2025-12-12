package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateDishRequest;
import org.example.utown_backend_nov18.dto.UpdateDishRequest;
import org.example.utown_backend_nov18.exception.NotFoundException;
import org.example.utown_backend_nov18.model.Dish;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final RestaurantService restaurantService;

    public DishService(DishRepository dishRepository,
                       RestaurantService restaurantService) {
        this.dishRepository = dishRepository;
        this.restaurantService = restaurantService;
    }

    @Transactional(readOnly = true)
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }

    @Transactional
    public Dish createDish(CreateDishRequest req) {
        Restaurant restaurant = restaurantService.findByIdOrThrow(req.getRestaurantId());

        Dish d = new Dish();
        d.setName(safeTrim(req.getName()));
        d.setPrice(req.getPrice());
        d.setDescription(safeTrim(req.getDescription()));
        d.setRestaurant(restaurant);

        return dishRepository.save(d);
    }

    @Transactional
    public Dish updateDish(Long id, UpdateDishRequest req) {
        Dish existing = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found: " + id));

        Restaurant restaurant = restaurantService.findByIdOrThrow(req.getRestaurantId());

        existing.setName(safeTrim(req.getName()));
        existing.setPrice(req.getPrice());
        existing.setDescription(safeTrim(req.getDescription()));
        existing.setRestaurant(restaurant);

        return dishRepository.save(existing);
    }

    @Transactional
    public void deleteDish(Long id) {
        Dish existing = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found: " + id));
        dishRepository.delete(existing);
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
