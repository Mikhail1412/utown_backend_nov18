package org.example.utown_backend_nov18.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateRestaurantTableRequest;
import org.example.utown_backend_nov18.dto.RestaurantTableDto;
import org.example.utown_backend_nov18.dto.UpdateRestaurantTableRequest;
import org.example.utown_backend_nov18.model.DiningArea;
import org.example.utown_backend_nov18.model.RestaurantTable;
import org.example.utown_backend_nov18.service.DiningAreaService;
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
public class RestaurantTableController {

    private final RestaurantTableService tableService;
    private final DiningAreaService diningAreaService;

    public RestaurantTableController(RestaurantTableService tableService,
                                     DiningAreaService diningAreaService) {
        this.tableService = tableService;
        this.diningAreaService = diningAreaService;
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

    @GetMapping
    public List<RestaurantTableDto> getAll() {
        log.info("GET /api/tables called");
        List<RestaurantTable> list = tableService.findAll();
        log.debug("Found {} tables", list.size());
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

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

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateRestaurantTableRequest req) {
        log.info("POST /api/tables called");

        Optional<DiningArea> areaOpt = diningAreaService.findById(req.getDiningAreaId());
        if (areaOpt.isEmpty()) {
            log.warn("DiningArea with id {} not found for table create", req.getDiningAreaId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("DiningArea not found");
        }

        RestaurantTable t = new RestaurantTable();
        t.setTableNumber(req.getTableNumber().trim());
        t.setCapacity(req.getCapacity());
        t.setActive(true);
        t.setDiningArea(areaOpt.get());

        RestaurantTable saved = tableService.save(t);
        log.info("Table created with id {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateRestaurantTableRequest req) {
        log.info("PUT /api/tables/{} called", id);

        Optional<RestaurantTable> tableOpt = tableService.findById(id);
        if (tableOpt.isEmpty()) {
            log.warn("Table with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }

        Optional<DiningArea> areaOpt = diningAreaService.findById(req.getDiningAreaId());
        if (areaOpt.isEmpty()) {
            log.warn("DiningArea with id {} not found for table update", req.getDiningAreaId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("DiningArea not found");
        }

        RestaurantTable existing = tableOpt.get();
        existing.setTableNumber(req.getTableNumber().trim());
        existing.setCapacity(req.getCapacity());
        existing.setActive(req.isActive());
        existing.setDiningArea(areaOpt.get());

        RestaurantTable saved = tableService.save(existing);
        log.info("Table with id {} updated", saved.getId());

        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tables/{} called", id);

        Optional<RestaurantTable> opt = tableService.findById(id);
        if (opt.isEmpty()) {
            log.warn("Table with id {} not found for delete", id);
            return ResponseEntity.notFound().build();
        }

        tableService.deleteById(id);
        log.info("Table with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
