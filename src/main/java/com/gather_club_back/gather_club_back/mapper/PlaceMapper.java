package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.entity.PlaceCategory;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import com.gather_club_back.gather_club_back.repository.PlaceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceMapper {
    
    private final PlaceCategoryRepository placeCategoryRepository;
    private final PlaceCategoryMapper placeCategoryMapper;
    
    public PlaceResponse toModel(Place entity) {
        if (entity == null) {
            return null;
        }

        PlaceResponse response = new PlaceResponse();
        response.setPlaceId(entity.getPlaceId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        response.setAddress(entity.getAddress());
        response.setCategoryId(entity.getCategoryId());
        response.setCreatedAt(entity.getCreatedAt());
        response.setIsApproved(entity.getIsApproved());
        response.setPopularity(entity.getPopularity());
        response.setImageUrl(entity.getImageUrl());
        
        // Добавляем информацию о категории
        if (entity.getCategoryId() != null) {
            placeCategoryRepository.findById(entity.getCategoryId())
                    .ifPresent(category -> response.setCategory(placeCategoryMapper.toModel(category)));
        }
        
        return response;
    }
}
