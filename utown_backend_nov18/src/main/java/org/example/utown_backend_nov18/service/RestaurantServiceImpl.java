package org.example.utown_backend_nov18.service;

import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateRestaurantRequest;
import org.example.utown_backend_nov18.dto.NotificationDto;
import org.example.utown_backend_nov18.dto.UpdateRestaurantRequest;
import org.example.utown_backend_nov18.exception.AccessDeniedDomainException;
import org.example.utown_backend_nov18.exception.BusinessConflictException;
import org.example.utown_backend_nov18.exception.NotFoundException;
import org.example.utown_backend_nov18.model.Restaurant;
import org.example.utown_backend_nov18.model.RestaurantStatus;
import org.example.utown_backend_nov18.model.User;
import org.example.utown_backend_nov18.repository.RestaurantRepository;
import org.example.utown_backend_nov18.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurants;
    private final UserRepository users;
    private final NotificationService notifications;

    public RestaurantServiceImpl(RestaurantRepository restaurants,
                                 UserRepository users,
                                 NotificationService notifications) {
        this.restaurants = restaurants;
        this.users = users;
        this.notifications = notifications;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> findAll() {
        return restaurants.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant findByIdOrThrow(Long id) {
        return restaurants.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + id));
    }

    @Override
    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, String currentUserEmail) {
        User owner = users.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("User not found: " + currentUserEmail));

        Restaurant r = new Restaurant();
        r.setName(safeTrim(req.getName()));
        r.setAddress(safeTrim(req.getAddress()));
        r.setPhoneNumber(safeTrim(req.getPhoneNumber()));
        r.setDescription(safeTrim(req.getDescription()));
        r.setOwner(owner);

        if (r.getStatus() == null) {
            r.setStatus(RestaurantStatus.OPEN);
        }

        Restaurant saved = restaurants.save(r);
        log.info("Restaurant {} created by {}", saved.getId(), currentUserEmail);
        return saved;
    }

    @Override
    @Transactional
    public Restaurant updateRestaurant(Long id, UpdateRestaurantRequest req, String currentUserEmail, boolean isAdmin) {
        Restaurant existing = findByIdOrThrow(id);
        assertOwnerOrAdmin(id, currentUserEmail, isAdmin);

        existing.setName(safeTrim(req.getName()));
        existing.setAddress(safeTrim(req.getAddress()));
        existing.setPhoneNumber(safeTrim(req.getPhoneNumber()));
        existing.setDescription(safeTrim(req.getDescription()));

        Restaurant saved = restaurants.save(existing);
        log.info("Restaurant {} updated by {}", id, currentUserEmail);
        return saved;
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long id, String currentUserEmail, boolean isAdmin) {
        assertOwnerOrAdmin(id, currentUserEmail, isAdmin);

        if (!restaurants.existsById(id)) {
            throw new NotFoundException("Restaurant not found: " + id);
        }

        restaurants.deleteById(id);
        log.info("Restaurant {} deleted by {}", id, currentUserEmail);
    }

    @Override
    @Transactional
    public Restaurant updateStatus(Long restaurantId, RestaurantStatus status, String currentUserEmail, boolean isAdmin) {
        if (status == null) {
            throw new BusinessConflictException("Restaurant status must not be null");
        }

        Restaurant restaurant = findByIdOrThrow(restaurantId);
        assertOwnerOrAdmin(restaurantId, currentUserEmail, isAdmin);

        RestaurantStatus oldStatus = restaurant.getStatus();
        if (oldStatus == status) {
            return restaurant;
        }

        restaurant.setStatus(status);
        Restaurant saved = restaurants.save(restaurant);

        log.info("Restaurant {} status changed {} -> {} by {}", restaurantId, oldStatus, status, currentUserEmail);

        NotificationDto payload = NotificationDto.now(
                "RESTAURANT_STATUS_CHANGED",
                null,
                null,
                saved.getId(),
                saved.getStatus() == null ? null : saved.getStatus().name(),
                "Restaurant " + saved.getId() + " status changed to " + saved.getStatus()
        );
        notifications.notifyRestaurant(saved.getId(), payload);

        return saved;
    }

    private void assertOwnerOrAdmin(Long restaurantId, String currentUserEmail, boolean isAdmin) {
        if (isAdmin) return;

        boolean isOwner = restaurants.existsByIdAndOwner_EmailIgnoreCase(restaurantId, currentUserEmail);
        if (!isOwner) {
            throw new AccessDeniedDomainException("Only owner/admin can modify this restaurant");
        }
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
