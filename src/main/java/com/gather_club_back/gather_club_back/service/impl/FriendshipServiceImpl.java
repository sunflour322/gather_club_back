package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Friendship;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.FriendshipMapper;
import com.gather_club_back.gather_club_back.model.FriendshipResponse;
import com.gather_club_back.gather_club_back.repository.FriendshipRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.FriendshipService;
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
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;

    @Override
    @Transactional
    public FriendshipResponse sendFriendRequest(Integer currentUserId, Integer friendId) {
        if (currentUserId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя отправить запрос в друзья самому себе");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        friendshipRepository.findFriendshipBetweenUsers(currentUser, friend)
                .ifPresent(f -> {
                    throw new IllegalStateException("Запрос в друзья уже существует");
                });

        FriendshipResponse friendshipModel = new FriendshipResponse()
                .setUser1Id(currentUserId)
                .setUser2Id(friendId)
                .setStatus("pending")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        Friendship friendship = friendshipMapper.toEntity(friendshipModel, currentUser, friend);
        return friendshipMapper.toModel(friendshipRepository.save(friendship));
    }

    @Override
    @Transactional
    public FriendshipResponse acceptFriendRequest(Integer currentUserId, Integer friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос в друзья не найден"));

        if (!friendship.getUser2().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("Нет прав для принятия этого запроса");
        }

        if (!"pending".equals(friendship.getStatus())) {
            throw new IllegalStateException("Запрос уже обработан");
        }

        friendship.setStatus("accepted")
                .setUpdatedAt(LocalDateTime.now());

        return friendshipMapper.toModel(friendshipRepository.save(friendship));
    }

    @Override
    @Transactional
    public FriendshipResponse rejectFriendRequest(Integer currentUserId, Integer friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос в друзья не найден"));

        if (!friendship.getUser2().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("Нет прав для отклонения этого запроса");
        }

        friendship.setStatus("rejected")
                .setUpdatedAt(LocalDateTime.now());

        return friendshipMapper.toModel(friendshipRepository.save(friendship));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponse> getAllFriends(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return friendshipRepository.findAllAcceptedFriendships(user)
                .stream()
                .map(friendshipMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponse> getPendingRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return friendshipRepository.findAllPendingFriendRequests(user)
                .stream()
                .map(friendshipMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponse> getAllRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return friendshipRepository.findAllRequests(user)
                .stream()
                .map(friendshipMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponse> getOutgoingRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return friendshipRepository.findOutgoingRequests(user)
                .stream()
                .map(friendshipMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponse> getIncomingRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return friendshipRepository.findIncomingRequests(user)
                .stream()
                .map(friendshipMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FriendshipResponse getFriendshipStatus(Integer userId, Integer friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
                
        return friendshipRepository.findFriendshipBetweenUsers(user, friend)
                .map(friendshipMapper::toModel)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteFriend(Integer userId, Integer friendId) {
        log.info("Начало процесса удаления дружбы между пользователями {} и {}", userId, friendId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        log.info("Найден пользователь с ID: {}", userId);
        
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        log.info("Найден друг с ID: {}", friendId);

        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(user, friend)
                .orElseThrow(() -> new EntityNotFoundException("Дружба не найдена"));
        log.info("Найдена запись о дружбе с ID: {}, статус: {}", friendship.getFriendshipId(), friendship.getStatus());

        if (!"accepted".equals(friendship.getStatus())) {
            log.error("Попытка удалить дружбу с неверным статусом: {}", friendship.getStatus());
            throw new IllegalStateException("Невозможно удалить дружбу, так как она не подтверждена");
        }

        try {
            friendshipRepository.delete(friendship);
            log.info("Дружба успешно удалена");
        } catch (Exception e) {
            log.error("Ошибка при удалении дружбы: {}", e.getMessage(), e);
            throw e;
        }
    }
} 