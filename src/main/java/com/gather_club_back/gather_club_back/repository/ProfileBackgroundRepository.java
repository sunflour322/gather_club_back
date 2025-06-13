package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.ProfileBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileBackgroundRepository extends JpaRepository<ProfileBackground, Integer> {
    
    List<ProfileBackground> findByIsActiveTrue();
    
    List<ProfileBackground> findByIsActiveTrueOrderByPriceAsc();
}
