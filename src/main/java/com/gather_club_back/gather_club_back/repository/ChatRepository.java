package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    Optional<Chat> findByMeetupMeetupId(Integer meetupId);
    
    @Query("""
            SELECT c, m, mp.status as participant_status
            FROM Chat c
            JOIN ChatParticipant cp ON c.chatId = cp.chat.chatId
            LEFT JOIN Meetup m ON c.meetup.meetupId = m.meetupId
            LEFT JOIN MeetupParticipant mp ON m.meetupId = mp.meetup.meetupId AND mp.user.userId = :userId
            WHERE cp.user.userId = :userId AND cp.leftAt IS NULL
            """)
    List<Object[]> findUserChatsWithMeetupInfo(@Param("userId") Integer userId);
    
    @Query("""
            SELECT c FROM Chat c
            LEFT JOIN FETCH c.meetup m
            LEFT JOIN FETCH c.createdBy u
            WHERE c.meetup.meetupId = :meetupId
            """)
    Optional<Chat> findByMeetupIdWithDetails(@Param("meetupId") Integer meetupId);

    @Query("""
            SELECT u.userId, u.username, u.avatarUrl, cp.role, cp.joinedAt
            FROM ChatParticipant cp
            JOIN cp.user u
            WHERE cp.chat.chatId = :chatId AND cp.leftAt IS NULL
            ORDER BY cp.role DESC, cp.joinedAt ASC
            """)
    List<Object[]> findChatParticipantsInfo(@Param("chatId") Integer chatId);
} 