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

    @PostMapping
    public ResponseEntity<MeetupResponse> createMeetup(@RequestBody MeetupRequest request) {
        return ResponseEntity.ok(meetupService.createMeetup(request));
    }

    @GetMapping("/{meetupId}")
    public ResponseEntity<MeetupResponse> getMeetup(@PathVariable Integer meetupId) {
        return ResponseEntity.ok(meetupService.getMeetup(meetupId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MeetupResponse>> getUserMeetups(@PathVariable Integer userId) {
        return ResponseEntity.ok(meetupService.getUserMeetups(userId));
    }

    @PostMapping("/{meetupId}/invite")
    public ResponseEntity<Void> inviteParticipants(
            @PathVariable Integer meetupId,
            @RequestBody List<Integer> userIds) {
        meetupService.inviteParticipants(meetupId, userIds);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{meetupId}/participants/{userId}")
    public ResponseEntity<MeetupResponse> updateParticipantStatus(
            @PathVariable Integer meetupId,
            @PathVariable Integer userId,
            @RequestParam String status) {
        return ResponseEntity.ok(meetupService.updateParticipantStatus(meetupId, userId, status));
    }

    @GetMapping("/invitations/{userId}")
    public ResponseEntity<List<MeetupResponse>> getInvitedMeetups(@PathVariable Integer userId) {
        return ResponseEntity.ok(meetupService.getInvitedMeetups(userId));
    }
} 