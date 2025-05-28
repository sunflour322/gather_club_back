package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Meetup;
import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import com.gather_club_back.gather_club_back.entity.Chat;
import com.gather_club_back.gather_club_back.entity.ChatParticipant;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.mapper.MeetupMapper;
import com.gather_club_back.gather_club_back.mapper.MeetupParticipantMapper;
import com.gather_club_back.gather_club_back.model.MeetupRequest;
import com.gather_club_back.gather_club_back.model.MeetupResponse;
import com.gather_club_back.gather_club_back.model.MeetupParticipantResponse;
import com.gather_club_back.gather_club_back.repository.*;
import com.gather_club_back.gather_club_back.service.MeetupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetupServiceImpl implements MeetupService {
    private final MeetupRepository meetupRepository;
    private final MeetupParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MeetupMapper meetupMapper;
    private final MeetupParticipantMapper participantMapper;

    @Override
    @Transactional
    public MeetupResponse createMeetup(MeetupRequest request) {
        User creator = userRepository.findById(request.getCreatorId())
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
                .setStatus("planned");

        meetup = meetupRepository.save(meetup);

        // Создаем групповой чат для встречи
        Chat chat = new Chat()
                .setName(request.getName())
                .setCreatedBy(creator)
                .setCreatedAt(LocalDateTime.now())
                .setIsGroup(true)
                .setMeetup(meetup)
                .setLastMessageAt(LocalDateTime.now());

        chat = chatRepository.save(chat);

        // Добавляем создателя как участника чата
        ChatParticipant creatorParticipant = new ChatParticipant()
                .setChat(chat)
                .setUser(creator)
                .setJoinedAt(LocalDateTime.now())
                .setRole("admin");

        chatParticipantRepository.save(creatorParticipant);

        // Приглашаем участников
        if (request.getInvitedUserIds() != null && !request.getInvitedUserIds().isEmpty()) {
            inviteParticipants(meetup.getMeetupId(), request.getInvitedUserIds());
        }

        List<MeetupParticipantResponse> participants = participantRepository
                .findByMeetupMeetupId(meetup.getMeetupId())
                .stream()
                .map(participantMapper::toModel)
                .collect(Collectors.toList());

        return meetupMapper.toModel(meetup, participants);
    }

    @Override
    @Transactional(readOnly = true)
    public MeetupResponse getMeetup(Integer meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));

        List<MeetupParticipantResponse> participants = participantRepository
                .findByMeetupMeetupId(meetupId)
                .stream()
                .map(participantMapper::toModel)
                .collect(Collectors.toList());

        return meetupMapper.toModel(meetup, participants);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetupResponse> getUserMeetups(Integer userId) {
        return meetupRepository.findByCreatorUserId(userId)
                .stream()
                .map(meetup -> {
                    List<MeetupParticipantResponse> participants = participantRepository
                            .findByMeetupMeetupId(meetup.getMeetupId())
                            .stream()
                            .map(participantMapper::toModel)
                            .collect(Collectors.toList());
                    return meetupMapper.toModel(meetup, participants);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MeetupResponse updateParticipantStatus(Integer meetupId, Integer userId, String status) {
        MeetupParticipant participant = participantRepository
                .findByMeetupMeetupIdAndUserUserId(meetupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Участник не найден"));

        participant.setStatus(status)
                .setRespondedAt(LocalDateTime.now());

        participantRepository.save(participant);

        // Если пользователь принял приглашение, добавляем его в чат встречи
        if ("accepted".equals(status)) {
            Chat chat = chatRepository.findByMeetupMeetupId(meetupId)
                    .orElseThrow(() -> new EntityNotFoundException("Чат встречи не найден"));

            if (!chatParticipantRepository.existsByChatChatIdAndUserUserId(chat.getChatId(), userId)) {
                ChatParticipant chatParticipant = new ChatParticipant()
                        .setChat(chat)
                        .setUser(participant.getUser())
                        .setJoinedAt(LocalDateTime.now())
                        .setRole("member");

                chatParticipantRepository.save(chatParticipant);
            }
        }

        return getMeetup(meetupId);
    }

    @Override
    @Transactional
    public void inviteParticipants(Integer meetupId, List<Integer> userIds) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));

        LocalDateTime now = LocalDateTime.now();

        userIds.forEach(userId -> {
            if (!participantRepository.existsByMeetupMeetupIdAndUserUserId(meetupId, userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + userId));

                MeetupParticipant participant = new MeetupParticipant()
                        .setMeetup(meetup)
                        .setUser(user)
                        .setStatus("invited")
                        .setInvitedAt(now);

                participantRepository.save(participant);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetupResponse> getInvitedMeetups(Integer userId) {
        return participantRepository.findByUserUserIdAndStatus(userId, "invited")
                .stream()
                .map(participant -> {
                    List<MeetupParticipantResponse> participants = participantRepository
                            .findByMeetupMeetupId(participant.getMeetup().getMeetupId())
                            .stream()
                            .map(participantMapper::toModel)
                            .collect(Collectors.toList());
                    return meetupMapper.toModel(participant.getMeetup(), participants);
                })
                .collect(Collectors.toList());
    }
} 