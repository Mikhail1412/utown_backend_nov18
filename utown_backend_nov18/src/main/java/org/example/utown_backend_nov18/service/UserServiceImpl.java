package org.example.utown_backend_nov18.service;

import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.CreateUserRequest;
import org.example.utown_backend_nov18.dto.UpdateUserRequest;
import org.example.utown_backend_nov18.dto.UserDto;
import org.example.utown_backend_nov18.model.Role;
import org.example.utown_backend_nov18.model.User;
import org.example.utown_backend_nov18.repository.RoleRepository;
import org.example.utown_backend_nov18.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository users;
    private final RoleRepository roles;

    public UserServiceImpl(UserRepository users, RoleRepository roles) {
        this.users = users;
        this.roles = roles;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("UserServiceImpl.findById called with id {}", id);
        return users.findById(id);
    }

    @Override
    public User create(String firstName,
                       String lastName,
                       int age,
                       String email,
                       String password,
                       List<Long> rolesIds) {

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(password);

        if (rolesIds != null && !rolesIds.isEmpty()) {
            Set<Role> r = new HashSet<>(roles.findAllById(rolesIds));
            user.setRoles(r);
        }

        user.setName(firstName + " " + lastName);

        return users.save(user);
    }

    @Override
    public User update(Long id,
                       String firstName,
                       String lastName,
                       int age,
                       String email,
                       String password,
                       List<Long> rolesIds) {

        User user = users.findById(id).orElseThrow();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(password);

        if (rolesIds != null && !rolesIds.isEmpty()) {
            Set<Role> r = new HashSet<>(roles.findAllById(rolesIds));
            user.setRoles(r);
        }

        user.setName(firstName + " " + lastName);

        return users.save(user);
    }

    @Override
    public void deleteById(Long id) {
        users.deleteById(id);
    }

    @Override
    public User save(User user) {
        return users.save(user);
    }

    @Override
    public List<User> findAll() {
        log.debug("UserServiceImpl.findAll called");
        return users.findAll();
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest req) {
        String firstName = req.getFirstName().trim();
        String lastName  = req.getLastName().trim();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(req.getAge());
        user.setEmail(req.getEmail().trim());
        user.setPassword(req.getPassword());

        if (req.getRoleIds() != null && !req.getRoleIds().isEmpty()) {
            Set<Role> roleSet = new HashSet<>(roles.findAllById(req.getRoleIds()));
            user.setRoles(roleSet);
        }

        user.setName(firstName + " " + lastName);

        User saved = users.save(user);
        log.info("User created with id {}", saved.getId());

        return toDto(saved);
    }

    @Override
    @Transactional
    public Optional<UserDto> updateUser(Long id, UpdateUserRequest req) {
        Optional<User> opt = users.findById(id);
        if (opt.isEmpty()) {
            log.warn("User with id {} not found for update", id);
            return Optional.empty();
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
            Set<Role> roleSet = new HashSet<>(roles.findAllById(req.getRoleIds()));
            existing.setRoles(roleSet);
        }

        User saved = users.save(existing);
        log.info("User with id {} updated", saved.getId());

        return Optional.of(toDto(saved));
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAge(user.getAge());
        dto.setEmail(user.getEmail());
        dto.setRoles(
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );
        return dto;
    }
}
