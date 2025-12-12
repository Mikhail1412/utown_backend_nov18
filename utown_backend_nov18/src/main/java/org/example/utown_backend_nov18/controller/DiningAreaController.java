package org.example.utown_backend_nov18.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateDiningAreaRequest;
import org.example.utown_backend_nov18.dto.DiningAreaDto;
import org.example.utown_backend_nov18.dto.UpdateDiningAreaRequest;
import org.example.utown_backend_nov18.model.DiningArea;
import org.example.utown_backend_nov18.service.DiningAreaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/dining-areas")
@CrossOrigin(origins = "*")
@Tag(name = "DiningAreas")
@SecurityRequirement(name = "bearerAuth")
public class DiningAreaController {

    private final DiningAreaService diningAreaService;

    public DiningAreaController(DiningAreaService diningAreaService) {
        this.diningAreaService = diningAreaService;
    }

    private DiningAreaDto toDto(DiningArea a) {
        DiningAreaDto dto = new DiningAreaDto();
        dto.setId(a.getId());
        dto.setName(a.getName());
        dto.setDescription(a.getDescription());
        dto.setRestaurantId(a.getRestaurant().getId());
        return dto;
    }

    @Operation(summary = "Get all dining areas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dining areas list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public List<DiningAreaDto> getAll() {
        log.info("GET /api/dining-areas called");
        List<DiningArea> list = diningAreaService.findAll();
        log.debug("Found {} dining areas", list.size());
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get dining area by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dining area"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Dining area not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DiningAreaDto> getById(@PathVariable Long id) {
        log.info("GET /api/dining-areas/{} called", id);
        Optional<DiningArea> opt = diningAreaService.findById(id);
        if (opt.isEmpty()) {
            log.warn("DiningArea with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    @Operation(summary = "Create dining area")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dining area created"),
            @ApiResponse(responseCode = "400", description = "Bad request (illegal argument / validation)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found (if validated in service)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (if restricted by role/ownership)"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateDiningAreaRequest req) {
        log.info("POST /api/dining-areas called");

        try {
            DiningArea saved = diningAreaService.createDiningArea(req);
            log.info("DiningArea created with id {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to create dining area: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @Operation(summary = "Update dining area")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dining area updated"),
            @ApiResponse(responseCode = "400", description = "Bad request (illegal argument / validation)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (if restricted by role/ownership)"),
            @ApiResponse(responseCode = "404", description = "Dining area not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateDiningAreaRequest req) {
        log.info("PUT /api/dining-areas/{} called", id);

        try {
            Optional<DiningArea> updatedOpt = diningAreaService.updateDiningArea(id, req);
            if (updatedOpt.isEmpty()) {
                log.warn("DiningArea with id {} not found for update", id);
                return ResponseEntity.notFound().build();
            }

            DiningArea updated = updatedOpt.get();
            log.info("DiningArea with id {} updated", updated.getId());
            return ResponseEntity.ok(toDto(updated));
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to update dining area {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @Operation(summary = "Delete dining area")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dining area deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Dining area not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/dining-areas/{} called", id);

        boolean deleted = diningAreaService.deleteDiningArea(id);
        if (!deleted) {
            log.warn("DiningArea with id {} not found for delete", id);
            return ResponseEntity.notFound().build();
        }

        log.info("DiningArea with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
