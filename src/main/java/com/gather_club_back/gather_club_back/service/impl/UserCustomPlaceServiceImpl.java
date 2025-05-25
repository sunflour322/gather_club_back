package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.PlaceCategory;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserCustomPlace;
import com.gather_club_back.gather_club_back.mapper.UserCustomPlaceMapper;
import com.gather_club_back.gather_club_back.model.UserCustomPlaceResponse;
import com.gather_club_back.gather_club_back.repository.PlaceCategoryRepository;
import com.gather_club_back.gather_club_back.repository.UserCustomPlaceRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.UserCustomPlaceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCustomPlaceServiceImpl implements UserCustomPlaceService {

    private final UserCustomPlaceRepository userCustomPlaceRepository;
    private final UserRepository userRepository;
    private final PlaceCategoryRepository placeCategoryRepository;
    private final UserCustomPlaceMapper userCustomPlaceMapper;

    @Override
    @Transactional
    public UserCustomPlaceResponse createPlace(Integer userId, UserCustomPlaceResponse placeResponse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        UserCustomPlace place = new UserCustomPlace()
                .setUser(user)
                .setName(placeResponse.getName())
                .setDescription(placeResponse.getDescription())
                .setLatitude(placeResponse.getLatitude())
                .setLongitude(placeResponse.getLongitude())
                .setCreatedAt(LocalDateTime.now())
                .setImageUrl(placeResponse.getImageUrl());

        if (placeResponse.getCategoryId() != null) {
            PlaceCategory category = placeCategoryRepository.findById(placeResponse.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
            place.setCategory(category);
        }

        return userCustomPlaceMapper.toModel(userCustomPlaceRepository.save(place));
    }

    @Override
    @Transactional
    public UserCustomPlaceResponse updatePlace(Integer userId, Integer placeId, UserCustomPlaceResponse placeResponse) {
        UserCustomPlace place = userCustomPlaceRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Место не найдено"));

        if (!place.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("Нет прав для редактирования этого места");
        }

        place.setName(placeResponse.getName())
                .setDescription(placeResponse.getDescription())
                .setLatitude(placeResponse.getLatitude())
                .setLongitude(placeResponse.getLongitude())
                .setImageUrl(placeResponse.getImageUrl());

        if (placeResponse.getCategoryId() != null) {
            PlaceCategory category = placeCategoryRepository.findById(placeResponse.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
            place.setCategory(category);
        } else {
            place.setCategory(null);
        }

        return userCustomPlaceMapper.toModel(userCustomPlaceRepository.save(place));
    }

    @Override
    @Transactional
    public void deletePlace(Integer userId, Integer placeId) {
        UserCustomPlace place = userCustomPlaceRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Место не найдено"));

        if (!place.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("Нет прав для удаления этого места");
        }

        userCustomPlaceRepository.delete(place);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCustomPlaceResponse> getAllPlaces(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return userCustomPlaceRepository.findAllByUser(user)
                .stream()
                .map(userCustomPlaceMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCustomPlaceResponse> getPlacesInArea(Integer userId, Double minLat, Double maxLat, Double minLon, Double maxLon) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return userCustomPlaceRepository.findAllByUserInArea(user, minLat, maxLat, minLon, maxLon)
                .stream()
                .map(userCustomPlaceMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCustomPlaceResponse> getPlacesByCategory(Integer userId, Integer categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return userCustomPlaceRepository.findAllByUserAndCategory_CategoryId(user, categoryId)
                .stream()
                .map(userCustomPlaceMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserCustomPlaceResponse getPlace(Integer userId, Integer placeId) {
        UserCustomPlace place = userCustomPlaceRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Место не найдено"));

        if (!place.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("Нет прав для просмотра этого места");
        }

        return userCustomPlaceMapper.toModel(place);
    }
} 