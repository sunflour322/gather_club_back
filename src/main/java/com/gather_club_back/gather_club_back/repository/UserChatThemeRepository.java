package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.UserChatTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChatThemeRepository extends JpaRepository<UserChatTheme, Integer> {
    
    List<UserChatTheme> findByUserUserId(Integer userId);
    
    Optional<UserChatTheme> findByUserUserIdAndThemeThemeId(Integer userId, Integer themeId);
    
    Optional<UserChatTheme> findByUserUserIdAndIsActiveTrue(Integer userId);
    
    boolean existsByUserUserIdAndThemeThemeId(Integer userId, Integer themeId);
    
    @Modifying
    @Query("UPDATE UserChatTheme uct SET uct.isActive = false WHERE uct.user.userId = :userId AND uct.isActive = true")
    void deactivateAllUserThemes(Integer userId);
}
