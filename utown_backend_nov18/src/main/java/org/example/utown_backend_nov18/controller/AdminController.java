package org.example.utown_backend_nov18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.example.utown_backend_nov18.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/list";
    }

    private final RoleRepository roleRepository;

    public AdminController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}


