package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.Chat;
import com.gather_club_back.gather_club_back.entity.Message;
import com.gather_club_back.gather_club_back.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    Page<Message> findByChatOrderBySentAtDesc(Chat chat, Pageable pageable);
    Optional<Message> findFirstByChatOrderBySentAtDesc(Chat chat);
    int countByChatAndReadAtIsNullAndSenderIsNot(Chat chat, User currentUser);
    
    List<Message> findByChatChatIdOrderBySentAtDesc(Integer chatId, Pageable pageable);
    List<Message> findByChatChatIdAndReadAtIsNullAndSenderUserIdNot(Integer chatId, Integer userId);
    
    @Modifying
    @Query("UPDATE Message m SET m.readAt = :readAt " +
           "WHERE m.chat.chatId = :chatId " +
           "AND m.sender.userId != :userId " +
           "AND m.readAt IS NULL")
    void markMessagesAsRead(@Param("chatId") Integer chatId,
                           @Param("userId") Integer userId,
                           @Param("readAt") LocalDateTime readAt);
} 