package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.mapper.PlaceMapper;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import com.gather_club_back.gather_club_back.repository.PlaceRepository;
import com.gather_club_back.gather_club_back.service.PlaceService;
import com.gather_club_back.gather_club_back.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private static final double KM_TO_DEGREES = 111.32;

    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;
    private final StorageService storageService;

    @Override
    public List<PlaceResponse> getNearbyPlaces(double latitude, double longitude, double radiusKm) {
        validateCoordinates(latitude, longitude);
        validateRadius(radiusKm);

        double[] bounds = calculateBounds(latitude, longitude, radiusKm);
        log.debug("Searching places within bounds: minLat={}, maxLat={}, minLng={}, maxLng={}",
                bounds[0], bounds[1], bounds[2], bounds[3]);

        List<Place> places = placeRepository.findNearbyPlaces(
                bounds[0], bounds[1], bounds[2], bounds[3]);

        return places.stream()
                .map(placeMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlaceResponse updatePlaceImage(Integer placeId, MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IOException("Place not found with id: " + placeId));

        try {
            // Удаляем старое изображение, если есть
            if (place.getImageUrl() != null) {
                storageService.deleteImage(place.getImageUrl());
                log.info("Deleted old image for place {}", placeId);
            }

            // Загружаем новое изображение
            String filename = generateFilename(imageFile.getOriginalFilename(), placeId);
            String path = "places/" + placeId + "/" + filename;
            String imageUrl = storageService.uploadImage(imageFile, path);
            log.info("Uploaded new image for place {} to {}", placeId, imageUrl);

            // Обновляем место
            place.setImageUrl(imageUrl)
                .setIsApproved(true)
                .setCreatedAt(Instant.now());
            
            Place savedPlace = placeRepository.save(place);

            return placeMapper.toModel(savedPlace);
        } catch (IOException e) {
            log.error("Failed to update image for place {}", placeId, e);
            throw new IOException("Failed to update place image", e);
        }
    }

    private double[] calculateBounds(double lat, double lng, double radiusKm) {
        double latDelta = radiusKm / KM_TO_DEGREES;
        double lngDelta = radiusKm / (KM_TO_DEGREES * Math.cos(Math.toRadians(lat)));

        return new double[]{
                lat - latDelta, // minLat
                lat + latDelta, // maxLat
                lng - lngDelta, // minLng
                lng + lngDelta  // maxLng
        };
    }

    private String generateFilename(String originalFilename, Integer placeId) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "place_" + placeId + "_" + System.currentTimeMillis() + extension;
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude value");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude value");
        }
    }

    private void validateRadius(double radiusKm) {
        if (radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
    }
}