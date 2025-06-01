package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.MeetupRequest;
import com.gather_club_back.gather_club_back.model.MeetupResponse;

import java.util.List;

public interface MeetupService {
    MeetupResponse createMeetup(MeetupRequest request);
    MeetupResponse getMeetup(Integer meetupId);
    List<MeetupResponse> getUserMeetups(Integer userId);
    MeetupResponse updateParticipantStatus(Integer meetupId, Integer userId, String status);
    void inviteParticipants(Integer meetupId, List<Integer> userIds);
    List<MeetupResponse> getInvitedMeetups(Integer userId);
    
    // Новые методы
    List<MeetupResponse> getActiveMeetups(Integer userId);
    List<MeetupResponse> getCompletedMeetups(Integer userId);
    List<MeetupResponse> getPendingMeetups(Integer userId);
    List<MeetupResponse> getOwnedAndAcceptedMeetups(Integer userId);
    List<MeetupResponse> getArchivedMeetups(Integer userId);
    MeetupResponse acceptInvitation(Integer meetupId, Integer userId);
    MeetupResponse declineInvitation(Integer meetupId, Integer userId);
} 