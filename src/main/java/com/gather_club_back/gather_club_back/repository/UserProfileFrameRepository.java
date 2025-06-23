package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.ProfileFrame;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserProfileFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileFrameRepository extends JpaRepository<UserProfileFrame, Integer> {
    
    List<UserProfileFrame> findByUserUserId(Integer userId);
    
    Optional<UserProfileFrame> findByUserUserIdAndFrameFrameId(Integer userId, Integer frameId);
    
    Optional<UserProfileFrame> findByUserUserIdAndIsActiveTrue(Integer userId);
    
    boolean existsByUserUserIdAndFrameFrameId(Integer userId, Integer frameId);
    
    @Modifying
    @Query("UPDATE UserProfileFrame upf SET upf.isActive = false WHERE upf.user.userId = :userId AND upf.isActive = true")
    void deactivateAllUserFrames(Integer userId);
}
