package org.example.utown_backend_nov18.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateDishRequest;
import org.example.utown_backend_nov18.dto.DishDto;
import org.example.utown_backend_nov18.dto.UpdateDishRequest;
import org.example.utown_backend_nov18.exception.NotFoundException;
import org.example.utown_backend_nov18.model.Dish;
import org.example.utown_backend_nov18.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "*")
@Tag(name = "Dishes")
@SecurityRequirement(name = "bearerAuth")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
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

    @Operation(summary = "Get all dishes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dishes list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public List<DishDto> getAll() {
        log.info("GET /api/dishes called");
        return dishService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get dish by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dish"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Dish not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public DishDto getById(@PathVariable Long id) {
        log.info("GET /api/dishes/{} called", id);
        Dish d = dishService.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found: " + id));
        return toDto(d);
    }

    @Operation(summary = "Create dish")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dish created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found (if validated in service)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (if restricted by role/ownership)"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<DishDto> create(@Valid @RequestBody CreateDishRequest req) {
        log.info("POST /api/dishes called");
        Dish saved = dishService.createDish(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @Operation(summary = "Update dish")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dish updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (if restricted by role/ownership)"),
            @ApiResponse(responseCode = "404", description = "Dish not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public DishDto update(@PathVariable Long id,
                          @Valid @RequestBody UpdateDishRequest req) {
        log.info("PUT /api/dishes/{} called", id);
        Dish updated = dishService.updateDish(id, req);
        return toDto(updated);
    }

    @Operation(summary = "Delete dish")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dish deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (if restricted by role/ownership)"),
            @ApiResponse(responseCode = "404", description = "Dish not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/dishes/{} called", id);
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}
