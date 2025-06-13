package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.ProfileBackground;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserProfileBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileBackgroundRepository extends JpaRepository<UserProfileBackground, Integer> {
    
    List<UserProfileBackground> findByUserUserId(Integer userId);
    
    Optional<UserProfileBackground> findByUserUserIdAndBackgroundBackgroundId(Integer userId, Integer backgroundId);
    
    Optional<UserProfileBackground> findByUserUserIdAndIsActiveTrue(Integer userId);
    
    boolean existsByUserUserIdAndBackgroundBackgroundId(Integer userId, Integer backgroundId);
    
    @Modifying
    @Query("UPDATE UserProfileBackground upb SET upb.isActive = false WHERE upb.user.userId = :userId AND upb.isActive = true")
    void deactivateAllUserBackgrounds(Integer userId);
}
