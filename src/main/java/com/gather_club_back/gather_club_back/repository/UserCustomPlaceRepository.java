package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.UserCustomPlace;
import com.gather_club_back.gather_club_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCustomPlaceRepository extends JpaRepository<UserCustomPlace, Integer> {
    
    List<UserCustomPlace> findAllByUser(User user);
    
    @Query("SELECT p FROM UserCustomPlace p WHERE p.user = :user AND " +
           "p.latitude BETWEEN :minLat AND :maxLat AND " +
           "p.longitude BETWEEN :minLon AND :maxLon")
    List<UserCustomPlace> findAllByUserInArea(
            @Param("user") User user,
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );
    
    List<UserCustomPlace> findAllByUserAndCategory_CategoryId(User user, Integer categoryId);
} 