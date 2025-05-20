package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceResponse;
import com.gather_club_back.gather_club_back.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

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
}
