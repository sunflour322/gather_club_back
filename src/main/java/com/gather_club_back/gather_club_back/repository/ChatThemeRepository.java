package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.ChatTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatThemeRepository extends JpaRepository<ChatTheme, Integer> {
    
    List<ChatTheme> findByIsActiveTrue();
    
    List<ChatTheme> findByIsActiveTrueOrderByPriceAsc();
}
