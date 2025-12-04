package org.example.utown_backend_nov18.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateDishRequest;
import org.example.utown_backend_nov18.dto.DishDto;
import org.example.utown_backend_nov18.dto.UpdateDishRequest;
import org.example.utown_backend_nov18.model.Dish;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.service.DishService;
import org.example.utown_backend_nov18.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "*")
public class DishController {

    private final DishService dishService;
    private final RestaurantService restaurantService;

    public DishController(DishService dishService,
                          RestaurantService restaurantService) {
        this.dishService = dishService;
        this.restaurantService = restaurantService;
    }

    private DishDto toDto(Dish d) {
        DishDto dto = new DishDto();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setPrice(d.getPrice());
        dto.setDescription(d.getDescription());
        dto.setRestaurantId(d.getRestaurant().getId());
        return dto;
    }

    @GetMapping
    public List<DishDto> getAll() {
        log.info("GET /api/dishes called");
        List<Dish> list = dishService.findAll();
        log.debug("Found {} dishes", list.size());
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDto> getById(@PathVariable Long id) {
        log.info("GET /api/dishes/{} called", id);
        Optional<Dish> opt = dishService.findById(id);
        if (opt.isEmpty()) {
            log.warn("Dish with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateDishRequest req) {
        log.info("POST /api/dishes called");

        Optional<Restaurant> restaurantOpt = restaurantService.findById(req.getRestaurantId());
        if (restaurantOpt.isEmpty()) {
            log.warn("Restaurant with id {} not found for dish create", req.getRestaurantId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Restaurant not found");
        }

        Dish d = new Dish();
        d.setName(req.getName().trim());
        d.setPrice(req.getPrice());
        d.setDescription(req.getDescription().trim());
        d.setRestaurant(restaurantOpt.get());

        Dish saved = dishService.save(d);
        log.info("Dish created with id {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateDishRequest req) {
        log.info("PUT /api/dishes/{} called", id);

        Optional<Dish> dishOpt = dishService.findById(id);
        if (dishOpt.isEmpty()) {
            log.warn("Dish with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }

        Optional<Restaurant> restaurantOpt = restaurantService.findById(req.getRestaurantId());
        if (restaurantOpt.isEmpty()) {
            log.warn("Restaurant with id {} not found for dish update", req.getRestaurantId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Restaurant not found");
        }

        Dish existing = dishOpt.get();
        existing.setName(req.getName().trim());
        existing.setPrice(req.getPrice());
        existing.setDescription(req.getDescription().trim());
        existing.setRestaurant(restaurantOpt.get());

        Dish saved = dishService.save(existing);
        log.info("Dish with id {} updated", saved.getId());

        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/dishes/{} called", id);

        Optional<Dish> opt = dishService.findById(id);
        if (opt.isEmpty()) {
            log.warn("Dish with id {} not found for delete", id);
            return ResponseEntity.notFound().build();
        }

        dishService.deleteById(id);
        log.info("Dish with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
