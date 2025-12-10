package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateRestaurantTableRequest;
import org.example.utown_backend_nov18.dto.UpdateRestaurantTableRequest;
import org.example.utown_backend_nov18.model.DiningArea;
import org.example.utown_backend_nov18.model.RestaurantTable;
import org.example.utown_backend_nov18.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final DiningAreaService diningAreaService;

    public RestaurantTableService(RestaurantTableRepository tableRepository,
                                  DiningAreaService diningAreaService) {
        this.tableRepository = tableRepository;
        this.diningAreaService = diningAreaService;
    }

    public List<RestaurantTable> findAll() {
        return tableRepository.findAll();
    }

    public Optional<RestaurantTable> findById(Long id) {
        return tableRepository.findById(id);
    }

    public RestaurantTable createTable(CreateRestaurantTableRequest req) {
        DiningArea area = diningAreaService.findById(req.getDiningAreaId())
                .orElseThrow(() -> new IllegalArgumentException("DiningArea not found"));

        RestaurantTable t = new RestaurantTable();
        t.setTableNumber(safeTrim(req.getTableNumber()));
        t.setCapacity(req.getCapacity());
        t.setActive(true);
        t.setDiningArea(area);

        return tableRepository.save(t);
    }

    public Optional<RestaurantTable> updateTable(Long id, UpdateRestaurantTableRequest req) {
        return tableRepository.findById(id)
                .map(existing -> {
                    DiningArea area = diningAreaService.findById(req.getDiningAreaId())
                            .orElseThrow(() -> new IllegalArgumentException("DiningArea not found"));

                    existing.setTableNumber(safeTrim(req.getTableNumber()));
                    existing.setCapacity(req.getCapacity());
                    existing.setActive(req.isActive());
                    existing.setDiningArea(area);

                    return tableRepository.save(existing);
                });
    }

    public boolean deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            return false;
        }
        tableRepository.deleteById(id);
        return true;
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
