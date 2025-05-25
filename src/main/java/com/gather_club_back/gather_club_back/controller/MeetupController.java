package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.MeetupRequest;
import com.gather_club_back.gather_club_back.model.MeetupResponse;
import com.gather_club_back.gather_club_back.service.MeetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetups")
@RequiredArgsConstructor
public class MeetupController {

    private final MeetupService meetupService;

    @PostMapping("/{userId}")
    public ResponseEntity<MeetupResponse> createMeetup(
            @PathVariable Integer userId,
            @RequestBody MeetupRequest request) {
        return ResponseEntity.ok(meetupService.createMeetup(userId, request));
    }

    @PutMapping("/{userId}/{meetupId}")
    public ResponseEntity<MeetupResponse> updateMeetup(
            @PathVariable Integer userId,
            @PathVariable Integer meetupId,
            @RequestBody MeetupRequest request) {
        return ResponseEntity.ok(meetupService.updateMeetup(userId, meetupId, request));
    }

    @DeleteMapping("/{userId}/{meetupId}")
    public ResponseEntity<Void> deleteMeetup(
            @PathVariable Integer userId,
            @PathVariable Integer meetupId) {
        meetupService.deleteMeetup(userId, meetupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/{meetupId}")
    public ResponseEntity<MeetupResponse> getMeetup(
            @PathVariable Integer userId,
            @PathVariable Integer meetupId) {
        return ResponseEntity.ok(meetupService.getMeetup(userId, meetupId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<MeetupResponse>> getUserMeetups(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(meetupService.getUserMeetups(userId));
    }

    @PostMapping("/{userId}/{meetupId}/invite")
    public ResponseEntity<Void> inviteParticipants(
            @PathVariable Integer userId,
            @PathVariable Integer meetupId,
            @RequestBody List<Integer> userIds) {
        meetupService.inviteParticipants(userId, meetupId, userIds);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/{meetupId}/status")
    public ResponseEntity<Void> updateParticipantStatus(
            @PathVariable Integer userId,
            @PathVariable Integer meetupId,
            @RequestBody String status) {
        meetupService.updateParticipantStatus(userId, meetupId, status);
        return ResponseEntity.ok().build();
    }
} 