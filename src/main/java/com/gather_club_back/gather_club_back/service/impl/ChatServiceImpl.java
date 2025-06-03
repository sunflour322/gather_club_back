package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Chat;
import com.gather_club_back.gather_club_back.entity.ChatParticipant;
import com.gather_club_back.gather_club_back.entity.Message;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.Meetup;
import com.gather_club_back.gather_club_back.mapper.MessageMapper;
import com.gather_club_back.gather_club_back.model.*;
import com.gather_club_back.gather_club_back.repository.ChatParticipantRepository;
import com.gather_club_back.gather_club_back.repository.ChatRepository;
import com.gather_club_back.gather_club_back.repository.MessageRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.ChatService;
import com.gather_club_back.gather_club_back.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatParticipantRepository participantRepository;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ChatResponse createChat(ChatRequest request) {
        User currentUser = userRepository.findById(userService.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        
        final Chat newChat = new Chat()
                .setName(request.getName())
                .setCreatedBy(currentUser)
                .setCreatedAt(LocalDateTime.now())
                .setIsGroup(request.getIsGroup())
                .setThemeId(request.getThemeId());
        
        if (request.getMeetupId() != null) {
            newChat.setMeetup(entityManager.getReference(Meetup.class, request.getMeetupId()));
        }
        
        final Chat savedChat = chatRepository.save(newChat);
        
        // Добавляем создателя как участника
        addParticipantToChat(savedChat, currentUser, "ADMIN");
        
        // Добавляем остальных участников
        if (request.getParticipantIds() != null) {
            request.getParticipantIds().stream()
                    .filter(id -> !id.equals(currentUser.getUserId()))
                    .forEach(userId -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
                        addParticipantToChat(savedChat, user, "MEMBER");
                    });
        }
        
        return mapChatToResponse(savedChat);
    }

    @Override
    public List<ChatResponse> getUserChats() {
        User currentUser = getCurrentUser();
        List<Object[]> chatsWithMeetupInfo = chatRepository.findUserChatsWithMeetupInfo(currentUser.getUserId());
        
        return chatsWithMeetupInfo.stream()
                .map(row -> {
                    Chat chat = (Chat) row[0];
                    Meetup meetup = (Meetup) row[1];
                    String participantStatus = (String) row[2];
                    
                    ChatResponse response = mapChatToResponse(chat);
                    
                    if (meetup != null) {
                        response.setMeetupName(meetup.getName());
                        response.setMeetupStatus(meetup.getStatus());
                        response.setMeetupScheduledTime(meetup.getScheduledTime());
                        response.setParticipantStatus(participantStatus);
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ChatResponse getChatById(Integer chatId) {
        Chat chat = getChatOrThrow(chatId);
        validateUserInChat(chat, getCurrentUser());
        return mapChatToResponse(chat);
    }

    @Override
    public List<ChatMessageResponse> getChatMessages(Integer chatId, int page, int size) {
        Chat chat = getChatOrThrow(chatId);
        validateUserInChat(chat, getCurrentUser());
        
        return messageRepository.findByChatOrderBySentAtDesc(chat, PageRequest.of(page, size))
                .stream()
                .map(messageMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatParticipantResponse> getChatParticipants(Integer chatId) {
        Chat chat = getChatOrThrow(chatId);
        validateUserInChat(chat, getCurrentUser());
        
        return participantRepository.findByChatAndLeftAtIsNull(chat)
                .stream()
                .map(this::mapParticipantToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatParticipantResponse addParticipant(Integer chatId, Integer userId) {
        Chat chat = getChatOrThrow(chatId);
        User currentUser = getCurrentUser();
        validateUserIsAdmin(chat, currentUser);
        
        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        
        ChatParticipant participant = addParticipantToChat(chat, userToAdd, "MEMBER");
        return mapParticipantToResponse(participant);
    }

    @Override
    @Transactional
    public void removeParticipant(Integer chatId, Integer userId) {
        Chat chat = getChatOrThrow(chatId);
        User currentUser = getCurrentUser();
        validateUserIsAdmin(chat, currentUser);
        
        ChatParticipant participant = participantRepository.findByChatAndUserAndLeftAtIsNull(chat, 
                userRepository.getReferenceById(userId))
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        
        participant.setLeftAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    @Override
    @Transactional
    public void deleteChat(Integer chatId) {
        Chat chat = getChatOrThrow(chatId);
        User currentUser = getCurrentUser();
        validateUserIsAdmin(chat, currentUser);
        
        participantRepository.findByChatAndLeftAtIsNull(chat)
                .forEach(participant -> {
                    participant.setLeftAt(LocalDateTime.now());
                    participantRepository.save(participant);
                });
    }

    @Override
    @Transactional
    public ChatMessageResponse saveAndProcessMessage(ChatMessageRequest request) {
        final Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Отправитель не найден"));

        Message replyTo = null;
        if (request.getReplyToId() != null) {
            replyTo = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new EntityNotFoundException("Сообщение для ответа не найдено"));
        }

        Message message = new Message()
                .setChat(chat)
                .setSender(sender)
                .setContent(request.getContent())
                .setSentAt(LocalDateTime.now())
                .setIsSystem(false)
                .setReplyTo(replyTo);

        message = messageRepository.save(message);

        // Обновляем время последнего сообщения в чате
        chat.setLastMessageAt(message.getSentAt());
        chatRepository.save(chat);

        return messageMapper.toModel(message);
    }

    @Override
    @Transactional
    public void markMessageAsRead(Integer chatId, Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        messageRepository.markMessagesAsRead(chatId, userId, now);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Integer chatId, Integer limit, Integer offset) {
        return messageRepository.findByChatChatIdOrderBySentAtDesc(chatId, PageRequest.of(offset, limit))
                .stream()
                .map(messageMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getUnreadMessages(Integer chatId, Integer userId) {
        return messageRepository.findByChatChatIdAndReadAtIsNullAndSenderUserIdNot(chatId, userId)
                .stream()
                .map(messageMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatResponse getChatByMeetupId(Integer meetupId) {
        Chat chat = chatRepository.findByMeetupIdWithDetails(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Чат для встречи не найден"));
        
        // Проверяем, что текущий пользователь имеет доступ к чату
        User currentUser = getCurrentUser();
        validateUserInChat(chat, currentUser);
        
        return mapChatToResponse(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatParticipantRequest> getChatParticipantsInfo(Integer chatId) {
        // Проверяем, что чат существует и пользователь имеет к нему доступ
        Chat chat = getChatOrThrow(chatId);
        validateUserInChat(chat, getCurrentUser());
        
        List<Object[]> participantsData = chatRepository.findChatParticipantsInfo(chatId);
        
        return participantsData.stream()
                .map(row -> new ChatParticipantRequest()
                        .setUserId((Integer) row[0])
                        .setUsername((String) row[1])
                        .setAvatarUrl((String) row[2])
                        .setRole((String) row[3])
                        .setJoinedAt((LocalDateTime) row[4]))
                .collect(Collectors.toList());
    }

    private Chat getChatOrThrow(Integer chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found: " + chatId));
    }

    private User getCurrentUser() {
        return userRepository.findById(userService.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
    }

    private void validateUserInChat(Chat chat, User user) {
        if (!participantRepository.existsByChatAndUserAndLeftAtIsNull(chat, user)) {
            throw new IllegalStateException("User is not a participant of this chat");
        }
    }

    private void validateUserIsAdmin(Chat chat, User user) {
        if (!participantRepository.existsByChatAndUserAndRoleAndLeftAtIsNull(chat, user, "ADMIN")) {
            throw new IllegalStateException("User is not an admin of this chat");
        }
    }

    private ChatParticipant addParticipantToChat(Chat chat, User user, String role) {
        ChatParticipant participant = new ChatParticipant()
                .setChat(chat)
                .setUser(user)
                .setRole(role)
                .setJoinedAt(LocalDateTime.now());
        return participantRepository.save(participant);
    }

    private ChatResponse mapChatToResponse(Chat chat) {
        ChatResponse response = new ChatResponse();
        response.setChatId(chat.getChatId());
        response.setName(chat.getName());
        response.setCreatedById(chat.getCreatedBy().getUserId());
        response.setCreatedByName(chat.getCreatedBy().getUsername());
        response.setCreatedByAvatar(chat.getCreatedBy().getAvatarUrl());
        response.setCreatedAt(chat.getCreatedAt());
        response.setIsGroup(chat.getIsGroup());
        response.setThemeId(chat.getThemeId());
        response.setMeetupId(chat.getMeetup() != null ? chat.getMeetup().getMeetupId() : null);
        response.setLastMessageAt(chat.getLastMessageAt());
        
        // Получаем последнее сообщение
        messageRepository.findFirstByChatOrderBySentAtDesc(chat)
                .ifPresent(message -> response.setLastMessageContent(message.getContent()));
        
        // Получаем количество непрочитанных сообщений
        response.setUnreadCount(messageRepository.countByChatAndReadAtIsNullAndSenderIsNot(
                chat, getCurrentUser()));
        
        return response;
    }

    private ChatParticipantResponse mapParticipantToResponse(ChatParticipant participant) {
        ChatParticipantResponse response = new ChatParticipantResponse();
        response.setParticipantId(participant.getParticipantId());
        response.setChatId(participant.getChat().getChatId());
        response.setUserId(participant.getUser().getUserId());
        response.setUserName(participant.getUser().getUsername());
        response.setUserAvatar(participant.getUser().getAvatarUrl());
        response.setJoinedAt(participant.getJoinedAt());
        response.setLeftAt(participant.getLeftAt());
        response.setRole(participant.getRole());
        return response;
    }
} 