package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Integer> {
    List<UserLocation> findByUserUserIdOrderByTimestampDesc(Integer userId);
    Optional<UserLocation> findFirstByUserUserIdOrderByTimestampDesc(Integer userId);
    List<UserLocation> findByUserUserIdAndIsPublicTrueOrderByTimestampDesc(Integer userId);
} 