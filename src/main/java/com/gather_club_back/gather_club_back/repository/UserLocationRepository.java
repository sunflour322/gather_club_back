package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Integer> {
    List<UserLocation> findByUserUserIdOrderByTimestampDesc(Integer userId);
    Optional<UserLocation> findFirstByUserUserIdOrderByTimestampDesc(Integer userId);
    List<UserLocation> findByUserUserIdAndIsPublicTrueOrderByTimestampDesc(Integer userId);
    
    @Query("SELECT ul FROM UserLocation ul WHERE ul.user.userId IN :userIds AND ul.isPublic = true AND " +
           "ul.timestamp = (SELECT MAX(ul2.timestamp) FROM UserLocation ul2 WHERE ul2.user.userId = ul.user.userId AND ul2.isPublic = true)")
    List<UserLocation> findLastPublicLocationsByUserIds(@Param("userIds") List<Integer> userIds);
} 