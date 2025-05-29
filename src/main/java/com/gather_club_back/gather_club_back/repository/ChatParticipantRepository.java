package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.Chat;
import com.gather_club_back.gather_club_back.entity.ChatParticipant;
import com.gather_club_back.gather_club_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Integer> {
    List<ChatParticipant> findByUserAndLeftAtIsNull(User user);
    List<ChatParticipant> findByChatAndLeftAtIsNull(Chat chat);
    Optional<ChatParticipant> findByChatAndUserAndLeftAtIsNull(Chat chat, User user);
    boolean existsByChatAndUserAndLeftAtIsNull(Chat chat, User user);
    boolean existsByChatAndUserAndRoleAndLeftAtIsNull(Chat chat, User user, String role);
} 