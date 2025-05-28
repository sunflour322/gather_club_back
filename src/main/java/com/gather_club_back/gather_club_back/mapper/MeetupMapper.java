package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.Meetup;
import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import com.gather_club_back.gather_club_back.model.MeetupResponse;
import com.gather_club_back.gather_club_back.model.MeetupParticipantResponse;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MeetupMapper {
    private final PlaceMapper placeMapper;
    private final UserMapper userMapper;
    private final MeetupParticipantMapper participantMapper;

    public MeetupResponse toModel(Meetup entity, List<MeetupParticipantResponse> participants) {
        if (entity == null) {
            return null;
        }

        MeetupResponse response = new MeetupResponse();
        response.setMeetupId(entity.getMeetupId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPlace(placeMapper.toModel(entity.getPlace()));
        response.setCreator(userMapper.toModel(entity.getCreator()));
        response.setScheduledTime(entity.getScheduledTime());
        response.setCreatedAt(entity.getCreatedAt());
        response.setStatus(entity.getStatus());
        response.setParticipants(participants);

        return response;
    }
} 