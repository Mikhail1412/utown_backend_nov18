package org.example.utown_backend_nov18.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.example.utown_backend_nov18.model.User;
import org.example.utown_backend_nov18.model.Role;
import org.example.utown_backend_nov18.repository.RoleRepository;
import org.example.utown_backend_nov18.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.utown_backend_nov18.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateUserRequest;
import org.example.utown_backend_nov18.dto.UpdateUserRequest;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAge(user.getAge());
        dto.setEmail(user.getEmail());
        dto.setRoles(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
        return dto;
    }

    private User fromDto(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAge(dto.getAge());
        user.setEmail(dto.getEmail());
        return user;
    }

    public UserController(UserService userService,
                          RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PermitAll
    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /api/users called");
        List<User> users = userService.findAll();
        log.debug("Found {} users", users.size());

        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} called", id);

        return userService.findById(id)
                .map(user -> {
                    // здесь
                    return toDto(user);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("User with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest req) {
        log.info("POST /api/users called");

        String firstName = req.getFirstName().trim();
        String lastName  = req.getLastName().trim();

        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setAge(req.getAge());
        u.setEmail(req.getEmail().trim());
        u.setPassword(req.getPassword());

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(req.getRoleIds()));
        u.setRoles(roles);
        u.setName(firstName + " " + lastName);

        User saved = userService.save(u);
        log.info("User created with id {}", saved.getId());

        UserDto dto = toDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @ApiResponse(responseCode = "404", description = "User not found")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                             @Valid @RequestBody UpdateUserRequest req) {
        log.info("PUT /api/users/{} called", id);

        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) {
            log.warn("PUT /api/users/{} - user not found", id);
            return ResponseEntity.notFound().build();
        }

        User existing = opt.get();

        String firstName = req.getFirstName().trim();
        String lastName  = req.getLastName().trim();

        existing.setFirstName(firstName);
        existing.setLastName(lastName);
        existing.setAge(req.getAge());
        existing.setEmail(req.getEmail().trim());
        existing.setName(firstName + " " + lastName);

        if (req.getRoleIds() != null && !req.getRoleIds().isEmpty()) {
            existing.setRoles(new HashSet<>(roleRepository.findAllById(req.getRoleIds())));
        }

        User saved = userService.save(existing);
        log.info("User with id {} updated", saved.getId());

        UserDto dto = toDto(saved);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} called", id);

        if (userService.findById(id).isEmpty()) {
            log.warn("DELETE /api/users/{} - user not found", id);
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
