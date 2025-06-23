package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Meetup;
import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import com.gather_club_back.gather_club_back.entity.Chat;
import com.gather_club_back.gather_club_back.entity.ChatParticipant;
import com.gather_club_back.gather_club_back.entity.Message;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.mapper.MeetupMapper;
import com.gather_club_back.gather_club_back.mapper.MeetupParticipantMapper;
import com.gather_club_back.gather_club_back.model.MeetupRequest;
import com.gather_club_back.gather_club_back.model.MeetupResponse;
import com.gather_club_back.gather_club_back.model.MeetupParticipantResponse;
import com.gather_club_back.gather_club_back.repository.*;
import com.gather_club_back.gather_club_back.service.MeetupService;
import com.gather_club_back.gather_club_back.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private final MessageRepository messageRepository;
    private final MeetupMapper meetupMapper;
    private final MeetupParticipantMapper participantMapper;
    private final UserService userService;

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

            User user = participant.getUser();
            
            // Проверяем, есть ли участник чата с заполненным полем leftAt
            Optional<ChatParticipant> existingLeftParticipant = chatParticipantRepository
                    .findByChatAndUserAndLeftAtIsNotNull(chat, user);
            
            if (existingLeftParticipant.isPresent()) {
                // Если участник найден, очищаем поле leftAt
                ChatParticipant chatParticipant = existingLeftParticipant.get();
                chatParticipant.setLeftAt(null);
                chatParticipantRepository.save(chatParticipant);
            } else if (!chatParticipantRepository.existsByChatAndUserAndLeftAtIsNull(chat, user)) {
                // Если участника нет (ни с leftAt, ни без), создаем нового
                ChatParticipant chatParticipant = new ChatParticipant()
                        .setChat(chat)
                        .setUser(user)
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
        final Meetup meetup = meetupRepository.findById(meetupId)
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
    public List<MeetupResponse> getActiveMeetups(Integer userId) {
        List<Meetup> meetups = meetupRepository.findActiveMeetups(userId);
        return meetups.stream()
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
    @Transactional(readOnly = true)
    public List<MeetupResponse> getArchivedMeetups(Integer userId) {
        List<Meetup> meetups = meetupRepository.findArchivedMeetups(userId);
        return meetups.stream()
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
    @Transactional(readOnly = true)
    public List<MeetupResponse> getInvitedMeetups(Integer userId) {
        List<Meetup> meetups = meetupRepository.findInvitedMeetups(userId);
        return meetups.stream()
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
    @Transactional(readOnly = true)
    public List<MeetupResponse> getCompletedMeetups(Integer userId) {
        List<MeetupParticipant> participants = participantRepository
                .findByUserUserIdAndStatusAndMeetupStatusIn(
                    userId,
                    "accepted",
                    List.of("completed", "cancelled")
                );

        return participants.stream()
                .map(participant -> {
                    Meetup meetup = participant.getMeetup();
                    List<MeetupParticipantResponse> meetupParticipants = participantRepository
                            .findByMeetupMeetupId(meetup.getMeetupId())
                            .stream()
                            .map(participantMapper::toModel)
                            .collect(Collectors.toList());
                    return meetupMapper.toModel(meetup, meetupParticipants);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetupResponse> getPendingMeetups(Integer userId) {
        List<MeetupParticipant> participants = participantRepository
                .findByUserUserIdAndStatus(userId, "invited");

        return participants.stream()
                .map(participant -> {
                    Meetup meetup = participant.getMeetup();
                    List<MeetupParticipantResponse> meetupParticipants = participantRepository
                            .findByMeetupMeetupId(meetup.getMeetupId())
                            .stream()
                            .map(participantMapper::toModel)
                            .collect(Collectors.toList());
                    return meetupMapper.toModel(meetup, meetupParticipants);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetupResponse> getOwnedAndAcceptedMeetups(Integer userId) {
        // Получаем встречи, где пользователь является создателем и статус planned
        List<Meetup> ownedMeetups = meetupRepository.findByCreatorUserIdAndStatus(userId, "planned");
        
        // Получаем встречи, где пользователь принял приглашение
        List<MeetupParticipant> acceptedParticipations = participantRepository
                .findByUserUserIdAndStatus(userId, "accepted");
        
        // Объединяем результаты
        List<MeetupResponse> result = ownedMeetups.stream()
                .map(meetup -> {
                    List<MeetupParticipantResponse> participants = participantRepository
                            .findByMeetupMeetupId(meetup.getMeetupId())
                            .stream()
                            .map(participantMapper::toModel)
                            .collect(Collectors.toList());
                    return meetupMapper.toModel(meetup, participants);
                })
                .collect(Collectors.toList());
        
        // Добавляем встречи, где пользователь является принятым участником
        result.addAll(acceptedParticipations.stream()
                .map(participant -> {
                    Meetup meetup = participant.getMeetup();
                    List<MeetupParticipantResponse> participants = participantRepository
                            .findByMeetupMeetupId(meetup.getMeetupId())
                            .stream()
                            .map(participantMapper::toModel)
                            .collect(Collectors.toList());
                    return meetupMapper.toModel(meetup, participants);
                })
                .collect(Collectors.toList()));
        
        return result;
    }

    @Override
    @Transactional
    public MeetupResponse acceptInvitation(Integer meetupId, Integer userId) {
        return updateParticipantStatus(meetupId, userId, "accepted");
    }

    @Override
    @Transactional
    public MeetupResponse declineInvitation(Integer meetupId, Integer userId) {
        return updateParticipantStatus(meetupId, userId, "declined");
    }
    
    @Override
    @Transactional
    public MeetupResponse cancelMeetup(Integer meetupId, Integer userId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));
        
        // Проверяем, что пользователь является создателем встречи
        if (!meetup.getCreator().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Только создатель встречи может отменить встречу");
        }
        
        // Изменяем статус встречи на "cancelled"
        meetup.setStatus("cancelled");
        meetup = meetupRepository.save(meetup);
        
        List<MeetupParticipantResponse> participants = participantRepository
                .findByMeetupMeetupId(meetup.getMeetupId())
                .stream()
                .map(participantMapper::toModel)
                .collect(Collectors.toList());
        
        return meetupMapper.toModel(meetup, participants);
    }
    
    @Override
    @Transactional
    public MeetupResponse updateMeetup(Integer meetupId, Integer userId, MeetupRequest request) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));
        
        // Проверяем, что пользователь является создателем встречи
        if (!meetup.getCreator().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Только создатель встречи может редактировать встречу");
        }
        
        // Обновляем место, если оно изменилось и существует
        if (request.getPlaceId() != null && !request.getPlaceId().equals(meetup.getPlace().getPlaceId())) {
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new EntityNotFoundException("Место не найдено с ID: " + request.getPlaceId()));
            
            // Проверяем, что место одобрено
            if (place.getIsApproved() == null || !place.getIsApproved()) {
                throw new IllegalArgumentException("Место с ID: " + request.getPlaceId() + " не одобрено");
            }
            
            meetup.setPlace(place);
        }
        
        // Обновляем основные данные встречи
        if (request.getName() != null) {
            meetup.setName(request.getName());
            
            // Обновляем название чата встречи, если оно изменилось
            Chat chat = chatRepository.findByMeetupMeetupId(meetupId)
                    .orElseThrow(() -> new EntityNotFoundException("Чат встречи не найден"));
            chat.setName(request.getName());
            chatRepository.save(chat);
        }
        
        if (request.getDescription() != null) {
            meetup.setDescription(request.getDescription());
        }
        
        if (request.getScheduledTime() != null) {
            meetup.setScheduledTime(request.getScheduledTime());
        }
        
        meetup = meetupRepository.save(meetup);
        
        // Приглашаем новых участников, если они указаны
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
    public List<MeetupParticipantResponse> getMeetupParticipants(Integer meetupId) {
        // Проверяем, существует ли встреча
        if (!meetupRepository.existsById(meetupId)) {
            throw new EntityNotFoundException("Встреча с ID " + meetupId + " не найдена");
        }
        
        // Получаем всех участников встречи
        return participantRepository
                .findByMeetupMeetupId(meetupId)
                .stream()
                .map(participantMapper::toModel)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public MeetupResponse removeParticipant(Integer meetupId, Integer userId) {
        // Проверяем, существует ли встреча
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));
        
        // Проверяем, является ли пользователь участником встречи
        MeetupParticipant participant = participantRepository
                .findByMeetupMeetupIdAndUserUserId(meetupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Участник не найден"));
        
        // Удаляем участника встречи
        participantRepository.delete(participant);
        
        // Находим чат, связанный с этой встречей
        Chat chat = chatRepository.findByMeetupMeetupId(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Чат встречи не найден"));
        
        // Находим участника чата
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        // Помечаем участника чата как покинувшего чат
        ChatParticipant chatParticipant = chatParticipantRepository
                .findByChatAndUserAndLeftAtIsNull(chat, user)
                .orElse(null);
        
        if (chatParticipant != null) {
            chatParticipant.setLeftAt(LocalDateTime.now());
            chatParticipantRepository.save(chatParticipant);
            
            // Добавляем системное сообщение о том, что пользователь покинул чат
            Message systemMessage = new Message()
                    .setChat(chat)
                    .setSender(meetup.getCreator()) // Отправитель - создатель встречи
                    .setContent(user.getUsername() + " покинул(а) встречу")
                    .setSentAt(LocalDateTime.now())
                    .setIsSystem(true);
            
            messageRepository.save(systemMessage);
        }
        
        // Получаем обновленный список участников
        List<MeetupParticipantResponse> participants = participantRepository
                .findByMeetupMeetupId(meetupId)
                .stream()
                .map(participantMapper::toModel)
                .collect(Collectors.toList());
        
        return meetupMapper.toModel(meetup, participants);
    }
}
