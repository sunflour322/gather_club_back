package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    Optional<Chat> findByMeetupMeetupId(Integer meetupId);
} 