package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.model.User;
import org.example.utown_backend_nov18.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository users;

    public Optional<User> findByEmail(String email) {
        return users.findByEmail(email);
    }

    public UserServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public Optional<User> findById(Long id) {
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
        user.setEmail(email);
        user.setPassword(password);

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
        user.setEmail(email);
        user.setPassword(password);

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

    public List<User> findAll() {
        return users.findAll();
    }
}