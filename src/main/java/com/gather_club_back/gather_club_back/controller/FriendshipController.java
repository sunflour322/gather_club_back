package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.FriendshipResponse;
import com.gather_club_back.gather_club_back.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/request/{userId}/{friendId}")
    public ResponseEntity<FriendshipResponse> sendFriendRequest(
            @PathVariable Integer userId,
            @PathVariable Integer friendId) {
        return ResponseEntity.ok(friendshipService.sendFriendRequest(userId, friendId));
    }

    @PostMapping("/{userId}/{friendshipId}/accept")
    public ResponseEntity<FriendshipResponse> acceptFriendRequest(
            @PathVariable Integer userId,
            @PathVariable Integer friendshipId) {
        return ResponseEntity.ok(friendshipService.acceptFriendRequest(userId, friendshipId));
    }

    @PostMapping("/{userId}/{friendshipId}/reject")
    public ResponseEntity<FriendshipResponse> rejectFriendRequest(
            @PathVariable Integer userId,
            @PathVariable Integer friendshipId) {
        return ResponseEntity.ok(friendshipService.rejectFriendRequest(userId, friendshipId));
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<FriendshipResponse>> getAllFriends(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipService.getAllFriends(userId));
    }

    @GetMapping("/{userId}/pending")
    public ResponseEntity<List<FriendshipResponse>> getPendingRequests(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipService.getPendingRequests(userId));
    }
} 