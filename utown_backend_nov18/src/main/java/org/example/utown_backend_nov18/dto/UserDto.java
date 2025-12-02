package org.example.utown_backend_nov18.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    Long id;
    String firstName;
    String lastName;
    Integer age;
    String email;
    Set<String> roles;
}
