package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.UserCustomPlaceResponse;
import java.util.List;

public interface UserCustomPlaceService {
    UserCustomPlaceResponse createPlace(Integer userId, UserCustomPlaceResponse place);
    UserCustomPlaceResponse updatePlace(Integer userId, Integer placeId, UserCustomPlaceResponse place);
    void deletePlace(Integer userId, Integer placeId);
    List<UserCustomPlaceResponse> getAllPlaces(Integer userId);
    List<UserCustomPlaceResponse> getPlacesInArea(Integer userId, Double minLat, Double maxLat, Double minLon, Double maxLon);
    List<UserCustomPlaceResponse> getPlacesByCategory(Integer userId, Integer categoryId);
    UserCustomPlaceResponse getPlace(Integer userId, Integer placeId);
} 