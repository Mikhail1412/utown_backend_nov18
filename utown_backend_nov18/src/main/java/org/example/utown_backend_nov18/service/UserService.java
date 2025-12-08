package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.dto.CreateUserRequest;
import org.example.utown_backend_nov18.dto.UpdateUserRequest;
import org.example.utown_backend_nov18.dto.UserDto;
import org.example.utown_backend_nov18.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User save(User user);
    User create(String firstName,
                String lastName,
                int age,
                String email,
                String rawPassword,
                List<Long> roleIds);
    User update(Long id,
                String firstName,
                String lastName,
                int age,
                String email,
                String rawPassword,
                List<Long> roleIds);
    void deleteById(Long id);

    UserDto createUser(CreateUserRequest req);

    Optional<UserDto> updateUser(Long id, UpdateUserRequest req);

}
