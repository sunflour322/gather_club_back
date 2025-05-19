package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {
    public PlaceResponse toPlaceResponse(Place place) {
        return new PlaceResponse()
                .setPlaceId(place.getPlaceId())
                .setName(place.getName())
                .setDescription(place.getDescription())
                .setLatitude(place.getLatitude())
                .setLongitude(place.getLongitude())
                .setAddress(place.getAddress())
                .setCategoryId(place.getCategoryId() != null ? place.getCategoryId() : null)
                .setPopularity(place.getPopularity())
                .setImageUrl(place.getImageUrl());
    }
}
