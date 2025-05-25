package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Meetup;
import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.MeetupMapper;
import com.gather_club_back.gather_club_back.model.MeetupRequest;
import com.gather_club_back.gather_club_back.model.MeetupResponse;
import com.gather_club_back.gather_club_back.repository.MeetupParticipantRepository;
import com.gather_club_back.gather_club_back.repository.MeetupRepository;
import com.gather_club_back.gather_club_back.repository.PlaceRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.MeetupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetupServiceImpl implements MeetupService {

    private final MeetupRepository meetupRepository;
    private final MeetupParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final MeetupMapper meetupMapper;

    @Override
    @Transactional
    public MeetupResponse createMeetup(Integer creatorId, MeetupRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Место не найдено"));

        Meetup meetup = new Meetup()
                .setCreator(creator)
                .setPlace(place)
                .setName(request.getName())
                .setDescription(request.getDescription())
                .setScheduledTime(request.getScheduledTime())
                .setCreatedAt(LocalDateTime.now())
                .setStatus("ACTIVE");

        meetup = meetupRepository.save(meetup);

        // Добавляем создателя как участника
        MeetupParticipant creatorParticipant = new MeetupParticipant()
                .setMeetup(meetup)
                .setUser(creator)
                .setStatus("ACCEPTED")
                .setInvitedAt(LocalDateTime.now())
                .setRespondedAt(LocalDateTime.now());

        participantRepository.save(creatorParticipant);

        // Приглашаем других участников
        if (request.getInvitedUserIds() != null && !request.getInvitedUserIds().isEmpty()) {
            inviteParticipants(creatorId, meetup.getMeetupId(), request.getInvitedUserIds());
        }

        List<MeetupParticipant> participants = participantRepository.findByMeetupMeetupId(meetup.getMeetupId());
        return meetupMapper.toModel(meetup, participants);
    }

    @Override
    @Transactional
    public MeetupResponse updateMeetup(Integer userId, Integer meetupId, MeetupRequest request) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));

        if (!meetup.getCreator().getUserId().equals(userId)) {
            throw new IllegalStateException("Только создатель может редактировать встречу");
        }

        if (request.getPlaceId() != null && !request.getPlaceId().equals(meetup.getPlace().getPlaceId())) {
            Place newPlace = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new EntityNotFoundException("Место не найдено"));
            meetup.setPlace(newPlace);
        }

        meetup.setName(request.getName())
                .setDescription(request.getDescription())
                .setScheduledTime(request.getScheduledTime());

        meetup = meetupRepository.save(meetup);

        if (request.getInvitedUserIds() != null && !request.getInvitedUserIds().isEmpty()) {
            inviteParticipants(userId, meetupId, request.getInvitedUserIds());
        }

        List<MeetupParticipant> participants = participantRepository.findByMeetupMeetupId(meetupId);
        return meetupMapper.toModel(meetup, participants);
    }

    @Override
    @Transactional
    public void deleteMeetup(Integer userId, Integer meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));

        if (!meetup.getCreator().getUserId().equals(userId)) {
            throw new IllegalStateException("Только создатель может удалить встречу");
        }

        participantRepository.deleteByMeetupMeetupId(meetupId);
        meetupRepository.delete(meetup);
    }

    @Override
    @Transactional(readOnly = true)
    public MeetupResponse getMeetup(Integer userId, Integer meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));

        // Проверяем, является ли пользователь участником встречи
        boolean isParticipant = participantRepository.existsByMeetupMeetupIdAndUserUserId(meetupId, userId);
        if (!isParticipant) {
            throw new IllegalStateException("У вас нет доступа к этой встрече");
        }

        List<MeetupParticipant> participants = participantRepository.findByMeetupMeetupId(meetupId);
        return meetupMapper.toModel(meetup, participants);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetupResponse> getUserMeetups(Integer userId) {
        List<MeetupParticipant> participations = participantRepository.findByUserUserId(userId);
        List<MeetupResponse> responses = new ArrayList<>();

        for (MeetupParticipant participation : participations) {
            Meetup meetup = participation.getMeetup();
            List<MeetupParticipant> participants = participantRepository.findByMeetupMeetupId(meetup.getMeetupId());
            responses.add(meetupMapper.toModel(meetup, participants));
        }

        return responses;
    }

    @Override
    @Transactional
    public void inviteParticipants(Integer userId, Integer meetupId, List<Integer> userIds) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));

        if (!meetup.getCreator().getUserId().equals(userId)) {
            throw new IllegalStateException("Только создатель может приглашать участников");
        }

        LocalDateTime now = LocalDateTime.now();

        for (Integer invitedUserId : userIds) {
            if (!participantRepository.existsByMeetupMeetupIdAndUserUserId(meetupId, invitedUserId)) {
                User invitedUser = userRepository.findById(invitedUserId)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + invitedUserId));

                MeetupParticipant participant = new MeetupParticipant()
                        .setMeetup(meetup)
                        .setUser(invitedUser)
                        .setStatus("PENDING")
                        .setInvitedAt(now);

                participantRepository.save(participant);
            }
        }
    }

    @Override
    @Transactional
    public void updateParticipantStatus(Integer userId, Integer meetupId, String status) {
        MeetupParticipant participant = participantRepository.findByMeetupMeetupIdAndUserUserId(meetupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Приглашение не найдено"));

        participant.setStatus(status)
                .setRespondedAt(LocalDateTime.now());

        participantRepository.save(participant);
    }
} 