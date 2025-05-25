package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.MeetupRequest;
import com.gather_club_back.gather_club_back.model.MeetupResponse;

import java.util.List;

public interface MeetupService {
    MeetupResponse createMeetup(Integer creatorId, MeetupRequest request);
    MeetupResponse updateMeetup(Integer userId, Integer meetupId, MeetupRequest request);
    void deleteMeetup(Integer userId, Integer meetupId);
    MeetupResponse getMeetup(Integer userId, Integer meetupId);
    List<MeetupResponse> getUserMeetups(Integer userId);
    void inviteParticipants(Integer userId, Integer meetupId, List<Integer> userIds);
    void updateParticipantStatus(Integer userId, Integer meetupId, String status);
} 