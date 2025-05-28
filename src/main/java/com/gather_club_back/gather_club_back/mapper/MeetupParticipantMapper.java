package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import com.gather_club_back.gather_club_back.model.MeetupParticipantResponse;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetupParticipantMapper {
    private final UserMapper userMapper;

    public MeetupParticipantResponse toModel(MeetupParticipant entity) {
        if (entity == null) {
            return null;
        }

        MeetupParticipantResponse response = new MeetupParticipantResponse();
        response.setParticipantId(entity.getParticipantId());
        response.setMeetupId(entity.getMeetup().getMeetupId());
        response.setUser(userMapper.toModel(entity.getUser()));
        response.setStatus(entity.getStatus());
        response.setInvitedAt(entity.getInvitedAt());
        response.setRespondedAt(entity.getRespondedAt());

        return response;
    }
} 