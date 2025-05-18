package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.PlaceResponse;

import java.util.List;

public interface PlaceService {
    List<PlaceResponse> getNearbyPlaces(double latitude, double longitude, double radiusKm);
}
