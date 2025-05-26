package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.UserLocationRequest;
import com.gather_club_back.gather_club_back.model.UserLocationResponse;

import java.util.List;
import java.util.Optional;

public interface UserLocationService {
    UserLocationResponse updateLocation(Integer userId, UserLocationRequest request);
    List<UserLocationResponse> getUserLocations(Integer userId);
    Optional<UserLocationResponse> getLastLocation(Integer userId);
    List<UserLocationResponse> getPublicLocations(Integer userId);
} 