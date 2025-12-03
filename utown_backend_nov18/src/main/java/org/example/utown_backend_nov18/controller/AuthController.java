package org.example.utown_backend_nov18.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.utown_backend_nov18.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("POST /api/auth/login called for email={}", request.getEmail());

        if (request.getEmail() == null || request.getPassword() == null) {
            log.warn("Login failed: email or password is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and password must not be null");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String token = jwtService.generateToken(authentication);
            log.info("User {} successfully authenticated", request.getEmail());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException ex) {
            log.warn("Login failed for email {}: bad credentials", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }
}
