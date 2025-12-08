package org.example.utown_backend_nov18.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.utown_backend_nov18.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;

    public AdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(Model model) {
        log.info("GET /admin called");
        model.addAttribute("roles", roleService.findAll());
        return "admin/list";
    }
}
