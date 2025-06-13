package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.ProfileFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileFrameRepository extends JpaRepository<ProfileFrame, Integer> {
    
    List<ProfileFrame> findByIsActiveTrue();
    
    List<ProfileFrame> findByIsActiveTrueOrderByPriceAsc();
}
