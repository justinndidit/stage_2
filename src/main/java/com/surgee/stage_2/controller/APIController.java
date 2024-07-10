package com.surgee.stage_2.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.surgee.stage_2.services.APIService;
import com.surgee.stage_2.requests.CreateOrganizationRequest;
import com.surgee.stage_2.data.UserIdObj;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class APIController {
    private final APIService apiService;
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        return apiService.getUserProfile(userId);
    }

    @GetMapping("/organisations")
    public ResponseEntity<?> getUserOrganizations(@RequestHeader("Authorization") String token) {
        return apiService.getUserOrganizations(token);
    }

    @GetMapping("/organisations/{orgId}")
    public ResponseEntity<?> getOrganizationById(@PathVariable String orgId) {
        return apiService.getOrganizationById(orgId);
    }
    @PostMapping("/organisations")
    public ResponseEntity<?> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        return apiService
                        .createOrganization(
                            request.getName(),
                            request.getDescription()
                            );
    }

    @PostMapping("/organisations/{orgId}/users")
    public ResponseEntity<?> addUserToOrganization(@PathVariable String orgId, @RequestBody UserIdObj userIdObject) {
        return apiService.addUserToOrganization(orgId, userIdObject.getUserId());
    }
    
   

}
