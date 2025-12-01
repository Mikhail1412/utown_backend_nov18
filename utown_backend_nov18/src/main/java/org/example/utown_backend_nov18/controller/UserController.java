package org.example.utown_backend_nov18.controller;

import jakarta.annotation.security.PermitAll;
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
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    private static final String NAME_RE = "^[\\p{L}\\s-]+$";

    public UserController(UserService userService,
                          RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PermitAll
    @GetMapping
    public List<User> getAllUsers() {
        log.info("GET /api/users called");

        List<User> users = userService.findAll();

        log.debug("Found {} users", users.size());

        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} called", id);

        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("User with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest req) {

        if (req.firstName == null || !req.firstName.trim().matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Имя: только буквы / пробел / дефис");
        }
        if (req.lastName == null || !req.lastName.trim().matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Фамилия: только буквы / пробел / дефис");
        }
        if (req.age == null || req.age <= 0) {
            return ResponseEntity.badRequest().body("Возраст должен быть положительным числом");
        }
        if (req.email == null || req.email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email обязателен");
        }
        if (req.password == null || req.password.isBlank()) {
            return ResponseEntity.badRequest().body("Пароль обязателен");
        }
        if (req.roleIds == null || req.roleIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Нужно выбрать хотя бы одну роль");
        }

        User u = new User();
        u.setFirstName(req.firstName.trim());
        u.setLastName(req.lastName.trim());
        u.setAge(req.age);
        u.setEmail(req.email.trim());
        u.setPassword(req.password);
        u.setName(u.getFirstName() + " " + u.getLastName());
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(req.roleIds));
        u.setRoles(roles);

        User saved = userService.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                        @RequestBody UpdateUserRequest req) {

        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existing = opt.get();

        String firstName = req.firstName != null ? req.firstName.trim() : "";
        String lastName  = req.lastName  != null ? req.lastName.trim()  : "";

        if (firstName.isEmpty() || !firstName.matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Имя: только буквы / пробел / дефис");
        }
        if (lastName.isEmpty() || !lastName.matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Фамилия: только буквы / пробел / дефис");
        }
        if (req.age == null || req.age <= 0) {
            return ResponseEntity.badRequest().body("Возраст должен быть положительным числом");
        }
        if (req.email == null || req.email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email обязателен");
        }

        existing.setFirstName(firstName);
        existing.setLastName(lastName);
        existing.setAge(req.age);
        existing.setEmail(req.email.trim());
        existing.setName(firstName + " " + lastName);

        if (req.roleIds != null && !req.roleIds.isEmpty()) {
            existing.setRoles(new HashSet<>(roleRepository.findAllById(req.roleIds)));
        }

        User saved = userService.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateUserRequest {
        public String firstName;
        public String lastName;
        public Integer age;
        public String email;
        public String password;
        public List<Long> roleIds;
    }

    public static class UpdateUserRequest {
        public String firstName;
        public String lastName;
        public Integer age;
        public String email;
        public List<Long> roleIds;
    }
}