package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceImageRepository extends JpaRepository<PlaceImage, Integer> {
    List<PlaceImage> findByPlacePlaceId(Integer placeId);
    List<PlaceImage> findByPlacePlaceIdAndIsApprovedTrue(Integer placeId);
    List<PlaceImage> findByUploadedByUserId(Integer userId);
}
