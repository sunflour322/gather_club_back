package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.Meetup;
import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import com.gather_club_back.gather_club_back.model.MeetupResponse;
import com.gather_club_back.gather_club_back.model.ParticipantResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MeetupMapper {

    public MeetupResponse toModel(Meetup entity, List<MeetupParticipant> participants) {
        if (entity == null) {
            return null;
        }

        List<ParticipantResponse> participantResponses = participants.stream()
                .map(this::toParticipantModel)
                .collect(Collectors.toList());

        return new MeetupResponse()
                .setMeetupId(entity.getMeetupId())
                .setName(entity.getName())
                .setDescription(entity.getDescription())
                .setPlaceId(entity.getPlace() != null ? entity.getPlace().getPlaceId() : null)
                .setPlaceName(entity.getPlace() != null ? entity.getPlace().getName() : null)
                .setCreatorId(entity.getCreator().getUserId())
                .setCreatorName(entity.getCreator().getUsername())
                .setScheduledTime(entity.getScheduledTime())
                .setCreatedAt(entity.getCreatedAt())
                .setStatus(entity.getStatus())
                .setParticipants(participantResponses);
    }

    private ParticipantResponse toParticipantModel(MeetupParticipant participant) {
        return new ParticipantResponse()
                .setUserId(participant.getUser().getUserId())
                .setUsername(participant.getUser().getUsername())
                .setAvatarUrl(participant.getUser().getAvatarUrl())
                .setStatus(participant.getStatus())
                .setInvitedAt(participant.getInvitedAt())
                .setRespondedAt(participant.getRespondedAt());
    }
} 