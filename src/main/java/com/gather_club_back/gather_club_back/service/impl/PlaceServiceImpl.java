package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.mapper.PlaceMapper;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import com.gather_club_back.gather_club_back.repository.PlaceRepository;
import com.gather_club_back.gather_club_back.service.PlaceService;
import com.gather_club_back.gather_club_back.service.YandexDiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;
    private final YandexDiskService yandexDiskService;
    @Override
    public List<PlaceResponse> getNearbyPlaces(double latitude, double longitude, double radiusKm) {
        double[] bounds = calculateBounds(latitude, longitude, radiusKm);

        List<Place> places = placeRepository.findNearbyPlaces(
                bounds[0], bounds[1], bounds[2], bounds[3]);

        return places.stream()
                .map(placeMapper::toPlaceResponse)
                .collect(Collectors.toList());
    }

    private double[] calculateBounds(double lat, double lng, double radiusKm) {
        double latDelta = radiusKm / 111.32;
        double lngDelta = radiusKm / (111.32 * Math.cos(Math.toRadians(lat)));

        return new double[]{
                lat - latDelta, // minLat
                lat + latDelta, // maxLat
                lng - lngDelta, // minLng
                lng + lngDelta  // maxLng
        };
    }

    @Override
    public PlaceResponse updatePlaceImage(Integer placeId, MultipartFile imageFile) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        // Удаляем старое изображение, если есть
        if (place.getImageUrl() != null) {
            yandexDiskService.deleteImage(place.getImageUrl());
        }

        // Загружаем новое изображение
        String path = "places/" + placeId + "/" + imageFile.getOriginalFilename();
        String imageUrl = yandexDiskService.uploadImage(imageFile, path);

        // Обновляем место
        place.setImageUrl(imageUrl);
        Place savedPlace = placeRepository.save(place);

        return placeMapper.toPlaceResponse(savedPlace);
    }
}
