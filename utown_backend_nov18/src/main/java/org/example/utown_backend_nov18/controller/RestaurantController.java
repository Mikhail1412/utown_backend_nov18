package org.example.utown_backend_nov18.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateRestaurantRequest;
import org.example.utown_backend_nov18.dto.RestaurantDto;
import org.example.utown_backend_nov18.dto.UpdateRestaurantRequest;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.model.RestaurantStatus;
import org.example.utown_backend_nov18.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurants")
@SecurityRequirement(name = "bearerAuth")
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
        if (r.getStatus() != null) {
            dto.setStatus(r.getStatus().name());
        }
        if (r.getOwner() != null) {
            dto.setOwnerId(r.getOwner().getId());
        }
        return dto;
    }

    private boolean isAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @Operation(summary = "Get all restaurants")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurants list"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public List<RestaurantDto> getAll() {
        log.info("GET /api/restaurants called");
        List<Restaurant> list = service.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Operation(summary = "Get restaurant by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public RestaurantDto getById(@PathVariable Long id) {
        log.info("GET /api/restaurants/{} called", id);
        Restaurant r = service.findByIdOrThrow(id);
        return toDto(r);
    }

    @Operation(summary = "Create restaurant (owner/admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurant created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<RestaurantDto> create(@Valid @RequestBody CreateRestaurantRequest req,
                                                Authentication auth) {
        log.info("POST /api/restaurants called");
        String email = auth.getName();

        Restaurant saved = service.createRestaurant(req, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @Operation(summary = "Update restaurant (owner/admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public RestaurantDto update(@PathVariable Long id,
                                @Valid @RequestBody UpdateRestaurantRequest req,
                                Authentication auth) {
        log.info("PUT /api/restaurants/{} called", id);

        String email = auth.getName();
        boolean admin = isAdmin(auth);

        Restaurant saved = service.updateRestaurant(id, req, email, admin);
        return toDto(saved);
    }

    @Operation(summary = "Delete restaurant (owner/admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurant deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        log.info("DELETE /api/restaurants/{} called", id);

        String email = auth.getName();
        boolean admin = isAdmin(auth);

        service.deleteRestaurant(id, email, admin);
        return ResponseEntity.noContent().build();
    }

    public static class UpdateRestaurantStatusRequest {
        public String status;
    }

    @Operation(summary = "Update restaurant status OPEN/CLOSED (owner/admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Bad request (invalid status)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/{id}/status")
    public RestaurantDto updateStatus(@PathVariable Long id,
                                      @RequestBody UpdateRestaurantStatusRequest req,
                                      Authentication auth) {
        String email = auth.getName();
        boolean admin = isAdmin(auth);

        RestaurantStatus status = RestaurantStatus.valueOf(req.status.trim().toUpperCase());
        log.info("PATCH /api/restaurants/{}/status called with status={}", id, status);

        Restaurant updated = service.updateStatus(id, status, email, admin);
        return toDto(updated);
    }
}
