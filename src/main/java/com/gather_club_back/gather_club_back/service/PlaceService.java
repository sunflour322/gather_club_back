package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.PlaceRequest;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PlaceService {
    List<PlaceResponse> getNearbyPlaces(double latitude, double longitude, double radiusKm);
    PlaceResponse updatePlaceImage(Integer placeId, MultipartFile imageFile) throws IOException;
    
    // CRUD операции для администратора
    List<PlaceResponse> getAllPlaces();
    PlaceResponse getPlaceById(Integer placeId);
    PlaceResponse createPlace(PlaceRequest placeRequest);
    PlaceResponse updatePlace(Integer placeId, PlaceRequest placeRequest);
    void deletePlace(Integer placeId);
}
