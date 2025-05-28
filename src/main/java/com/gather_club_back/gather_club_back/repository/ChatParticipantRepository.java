package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Integer> {
    boolean existsByChatChatIdAndUserUserId(Integer chatId, Integer userId);
} 