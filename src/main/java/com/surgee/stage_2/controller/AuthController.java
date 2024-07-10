package com.surgee.stage_2.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import com.surgee.stage_2.model.User;
import com.surgee.stage_2.services.AuthService;
import com.surgee.stage_2.requests.LoginRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
    
        return authService.registerUser(user.getFirstName(),
                                            user.getLastName(),
                                           user.getEmail(),
                                           user.getPassword(),
                                           user.getPhone(),
                                           user.getRole());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        return authService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
    }
}
