package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateDishRequest;
import org.example.utown_backend_nov18.dto.UpdateDishRequest;
import org.example.utown_backend_nov18.model.Dish;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.repository.DishRepository;
import org.springframework.stereotype.Service;
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

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }

    public Dish createDish(CreateDishRequest req) {
        Restaurant restaurant = restaurantService.findById(req.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Dish d = new Dish();
        d.setName(safeTrim(req.getName()));
        d.setPrice(req.getPrice());
        d.setDescription(safeTrim(req.getDescription()));
        d.setRestaurant(restaurant);

        return dishRepository.save(d);
    }

    public Optional<Dish> updateDish(Long id, UpdateDishRequest req) {
        return dishRepository.findById(id)
                .map(existing -> {
                    Restaurant restaurant = restaurantService.findById(req.getRestaurantId())
                            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

                    existing.setName(safeTrim(req.getName()));
                    existing.setPrice(req.getPrice());
                    existing.setDescription(safeTrim(req.getDescription()));
                    existing.setRestaurant(restaurant);

                    return dishRepository.save(existing);
                });
    }

    public boolean deleteDish(Long id) {
        if (!dishRepository.existsById(id)) {
            return false;
        }
        dishRepository.deleteById(id);
        return true;
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
