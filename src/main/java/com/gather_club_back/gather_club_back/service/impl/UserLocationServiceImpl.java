package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserLocation;
import com.gather_club_back.gather_club_back.mapper.UserLocationMapper;
import com.gather_club_back.gather_club_back.model.UserLocationRequest;
import com.gather_club_back.gather_club_back.model.UserLocationResponse;
import com.gather_club_back.gather_club_back.repository.UserLocationRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.repository.FriendshipRepository;
import com.gather_club_back.gather_club_back.service.UserLocationService;
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
public class UserLocationServiceImpl implements UserLocationService {

    private final UserLocationRepository userLocationRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserLocationMapper userLocationMapper;

    @Override
    @Transactional
    public UserLocationResponse updateLocation(Integer userId, UserLocationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        UserLocation location = new UserLocation()
                .setUser(user)
                .setLatitude(request.getLatitude())
                .setLongitude(request.getLongitude())
                .setAccuracy(request.getAccuracy())
                .setAltitude(request.getAltitude())
                .setTimestamp(LocalDateTime.now())
                .setIsPublic(request.getIsPublic());

        return userLocationMapper.toModel(userLocationRepository.save(location));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLocationResponse> getUserLocations(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return userLocationRepository.findByUserUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(userLocationMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserLocationResponse> getLastLocation(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return userLocationRepository.findFirstByUserUserIdOrderByTimestampDesc(userId)
                .map(userLocationMapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLocationResponse> getPublicLocations(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return userLocationRepository.findByUserUserIdAndIsPublicTrueOrderByTimestampDesc(userId)
                .stream()
                .map(userLocationMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLocationResponse> getFriendsLastLocations(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        List<Integer> friendIds = friendshipRepository.findAllAcceptedFriendships(user)
                .stream()
                .map(friendship -> friendship.getUser1().getUserId().equals(userId) 
                        ? friendship.getUser2().getUserId() 
                        : friendship.getUser1().getUserId())
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return List.of();
        }

        return userLocationRepository.findLastPublicLocationsByUserIds(friendIds)
                .stream()
                .map(userLocationMapper::toModel)
                .collect(Collectors.toList());
    }
} 