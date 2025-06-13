package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import com.gather_club_back.gather_club_back.model.PlaceRequest;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import com.gather_club_back.gather_club_back.service.PlaceImageService;
import com.gather_club_back.gather_club_back.service.PlaceService;
import com.gather_club_back.gather_club_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;
    private final PlaceImageService placeImageService;
    private final UserService userService;
    
    // CRUD операции для администратора
    
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PlaceResponse>> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }
    
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> getPlaceById(@PathVariable Integer placeId) {
        return ResponseEntity.ok(placeService.getPlaceById(placeId));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PlaceResponse> createPlace(@RequestBody PlaceRequest placeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(placeService.createPlace(placeRequest));
    }
    
    @PutMapping("/{placeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PlaceResponse> updatePlace(
            @PathVariable Integer placeId,
            @RequestBody PlaceRequest placeRequest) {
        return ResponseEntity.ok(placeService.updatePlace(placeId, placeRequest));
    }
    
    @DeleteMapping("/{placeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePlace(@PathVariable Integer placeId) {
        placeService.deletePlace(placeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nearby")
    public List<PlaceResponse> getNearbyPlaces(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm) {

        return placeService.getNearbyPlaces(lat, lng, radiusKm);
    }

    @PostMapping("/{placeId}/image")
    public PlaceResponse updatePlaceImage(
            @PathVariable Integer placeId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            return placeService.updatePlaceImage(placeId, imageFile) ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/{placeId}/images")
    public ResponseEntity<List<PlaceImageResponse>> getPlaceImages(@PathVariable Integer placeId) {
        return ResponseEntity.ok(placeImageService.getPlaceImages(placeId));
    }

    @PostMapping("/{placeId}/images/add")
    public ResponseEntity<PlaceImageResponse> uploadPlaceImage(
            @PathVariable Integer placeId,
            @RequestParam("images") MultipartFile imageFile) throws IOException {

        Integer userId = userService.getUserId();
        return ResponseEntity.ok(
                placeImageService.uploadPlaceImage(placeId, imageFile, userId));
    }

    @PostMapping("/{placeId}/images/{imageId}/rate")
    public ResponseEntity<Void> rateImage(
            @PathVariable Integer placeId,
            @PathVariable Integer imageId,
            @RequestParam Boolean isLike) {

        placeImageService.rateImage(imageId, isLike);
        return ResponseEntity.ok().build();
    }
}
