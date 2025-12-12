package org.example.utown_backend_nov18.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateRestaurantTableRequest;
import org.example.utown_backend_nov18.dto.RestaurantTableDto;
import org.example.utown_backend_nov18.dto.UpdateRestaurantTableRequest;
import org.example.utown_backend_nov18.model.RestaurantTable;
import org.example.utown_backend_nov18.service.RestaurantTableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
@Tag(name = "RestaurantTables")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    public RestaurantTableController(RestaurantTableService tableService) {
        this.tableService = tableService;
    }

    private RestaurantTableDto toDto(RestaurantTable t) {
        RestaurantTableDto dto = new RestaurantTableDto();
        dto.setId(t.getId());
        dto.setTableNumber(t.getTableNumber());
        dto.setCapacity(t.getCapacity());
        dto.setActive(t.isActive());
        dto.setDiningAreaId(t.getDiningArea().getId());
        return dto;
    }

    @Operation(summary = "Get all tables")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tables list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public List<RestaurantTableDto> getAll() {
        log.info("GET /api/tables called");
        List<RestaurantTable> list = tableService.findAll();
        log.debug("Found {} tables", list.size());
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get table by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Table"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableDto> getById(@PathVariable Long id) {
        log.info("GET /api/tables/{} called", id);
        Optional<RestaurantTable> opt = tableService.findById(id);
        if (opt.isEmpty()) {
            log.warn("Table with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    @Operation(summary = "Create table")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Table created"),
            @ApiResponse(responseCode = "400", description = "Bad request (illegal argument / validation)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Dining area not found (if validated in service)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (if restricted by role/ownership)"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateRestaurantTableRequest req) {
        log.info("POST /api/tables called");

        try {
            RestaurantTable saved = tableService.createTable(req);
            log.info("Table created with id {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to create table: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @Operation(summary = "Update table")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Table updated"),
            @ApiResponse(responseCode = "400", description = "Bad request (illegal argument / validation)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (if restricted by role/ownership)"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateRestaurantTableRequest req) {
        log.info("PUT /api/tables/{} called", id);

        try {
            Optional<RestaurantTable> updatedOpt = tableService.updateTable(id, req);
            if (updatedOpt.isEmpty()) {
                log.warn("Table with id {} not found for update", id);
                return ResponseEntity.notFound().build();
            }

            RestaurantTable updated = updatedOpt.get();
            log.info("Table with id {} updated", updated.getId());
            return ResponseEntity.ok(toDto(updated));
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to update table {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @Operation(summary = "Delete table")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Table deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tables/{} called", id);

        boolean deleted = tableService.deleteTable(id);
        if (!deleted) {
            log.warn("Table with id {} not found for delete", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Table with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
