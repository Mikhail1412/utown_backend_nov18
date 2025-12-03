package org.example.utown_backend_nov18.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateRestaurantRequest;
import org.example.utown_backend_nov18.dto.RestaurantDto;
import org.example.utown_backend_nov18.dto.UpdateRestaurantRequest;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    private RestaurantDto toDto(Restaurant r) {
        RestaurantDto dto = new RestaurantDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setAddress(r.getAddress());
        dto.setPhoneNumber(r.getPhoneNumber());
        dto.setDescription(r.getDescription());
        return dto;
    }

    @GetMapping
    public List<RestaurantDto> getAll() {
        log.info("GET /api/restaurants called");
        List<Restaurant> list = service.findAll();
        log.debug("Found {} restaurants", list.size());
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getById(@PathVariable Long id) {
        log.info("GET /api/restaurants/{} called", id);
        Optional<Restaurant> opt = service.findById(id);
        if (opt.isEmpty()) {
            log.warn("Restaurant with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    @PostMapping
    public ResponseEntity<RestaurantDto> create(@Valid @RequestBody CreateRestaurantRequest req) {
        log.info("POST /api/restaurants called");

        Restaurant r = new Restaurant();
        r.setName(req.getName().trim());
        r.setAddress(req.getAddress().trim());
        r.setPhoneNumber(req.getPhoneNumber().trim());
        r.setDescription(req.getDescription().trim());

        Restaurant saved = service.save(r);
        log.info("Restaurant created with id {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDto> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateRestaurantRequest req) {
        log.info("PUT /api/restaurants/{} called", id);

        Optional<Restaurant> opt = service.findById(id);
        if (opt.isEmpty()) {
            log.warn("Restaurant with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        Restaurant existing = opt.get();
        existing.setName(req.getName().trim());
        existing.setAddress(req.getAddress().trim());
        existing.setPhoneNumber(req.getPhoneNumber().trim());
        existing.setDescription(req.getDescription().trim());

        Restaurant saved = service.save(existing);
        log.info("Restaurant with id {} updated", saved.getId());

        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/restaurants/{} called", id);

        Optional<Restaurant> opt = service.findById(id);
        if (opt.isEmpty()) {
            log.warn("Restaurant with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        service.deleteById(id);
        log.info("Restaurant with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
