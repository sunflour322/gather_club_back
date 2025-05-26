package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.UserLocationRequest;
import com.gather_club_back.gather_club_back.model.UserLocationResponse;
import com.gather_club_back.gather_club_back.service.UserLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/location")
@RequiredArgsConstructor
public class UserLocationController {

    private final UserLocationService userLocationService;

    @PostMapping
    public ResponseEntity<UserLocationResponse> updateLocation(
            @PathVariable Integer userId,
            @RequestBody UserLocationRequest request) {
        return ResponseEntity.ok(userLocationService.updateLocation(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<UserLocationResponse>> getUserLocations(@PathVariable Integer userId) {
        return ResponseEntity.ok(userLocationService.getUserLocations(userId));
    }

    @GetMapping("/last")
    public ResponseEntity<UserLocationResponse> getLastLocation(@PathVariable Integer userId) {
        return userLocationService.getLastLocation(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/public")
    public ResponseEntity<List<UserLocationResponse>> getPublicLocations(@PathVariable Integer userId) {
        return ResponseEntity.ok(userLocationService.getPublicLocations(userId));
    }
} 