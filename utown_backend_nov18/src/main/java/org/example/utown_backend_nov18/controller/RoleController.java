package org.example.utown_backend_nov18.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.dto.RoleDto;
import org.example.utown_backend_nov18.model.Role;
import org.example.utown_backend_nov18.repository.RoleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<RoleDto> getAllRoles() {
        log.info("GET /api/roles called");
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RoleDto toDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }
}

