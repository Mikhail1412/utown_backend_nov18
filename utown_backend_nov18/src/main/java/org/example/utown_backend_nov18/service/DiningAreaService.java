package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateDiningAreaRequest;
import org.example.utown_backend_nov18.dto.UpdateDiningAreaRequest;
import org.example.utown_backend_nov18.model.DiningArea;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.repository.DiningAreaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DiningAreaService {

    private final DiningAreaRepository diningAreaRepository;
    private final RestaurantService restaurantService;

    public DiningAreaService(DiningAreaRepository diningAreaRepository,
                             RestaurantService restaurantService) {
        this.diningAreaRepository = diningAreaRepository;
        this.restaurantService = restaurantService;
    }

    public List<DiningArea> findAll() {
        return diningAreaRepository.findAll();
    }

    public Optional<DiningArea> findById(Long id) {
        return diningAreaRepository.findById(id);
    }

    public DiningArea createDiningArea(CreateDiningAreaRequest req) {
        Restaurant restaurant = restaurantService.findByIdOrThrow(req.getRestaurantId());

        DiningArea a = new DiningArea();
        a.setName(safeTrim(req.getName()));
        a.setDescription(safeTrim(req.getDescription()));
        a.setRestaurant(restaurant);

        return diningAreaRepository.save(a);
    }

    public Optional<DiningArea> updateDiningArea(Long id, UpdateDiningAreaRequest req) {
        return diningAreaRepository.findById(id)
                .map(existing -> {
                    Restaurant restaurant = restaurantService.findByIdOrThrow(req.getRestaurantId());

                    existing.setName(safeTrim(req.getName()));
                    existing.setDescription(safeTrim(req.getDescription()));
                    existing.setRestaurant(restaurant);

                    return diningAreaRepository.save(existing);
                });
    }

    public boolean deleteDiningArea(Long id) {
        if (!diningAreaRepository.existsById(id)) {
            return false;
        }
        diningAreaRepository.deleteById(id);
        return true;
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
