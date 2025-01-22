package com.backend_training.app.controllers;

import com.auth0.exception.Auth0Exception;
import com.backend_training.app.services.Auth0Service;
import com.backend_training.app.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Auth0Service auth0Service;

    @Autowired
    public AuthController(Auth0Service auth0Service) {
        this.auth0Service = auth0Service;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) throws Auth0Exception {
        auth0Service.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) throws Auth0Exception {
        String accessToken = auth0Service.getAccessToken(user.getEmail(), user.getPassword());
        return ResponseEntity.ok("Access Token: " + accessToken);
    }
}
