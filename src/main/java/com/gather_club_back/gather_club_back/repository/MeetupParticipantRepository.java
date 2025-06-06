package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetupParticipantRepository extends JpaRepository<MeetupParticipant, Integer> {
    List<MeetupParticipant> findByMeetupMeetupId(Integer meetupId);
    Optional<MeetupParticipant> findByMeetupMeetupIdAndUserUserId(Integer meetupId, Integer userId);
    boolean existsByMeetupMeetupIdAndUserUserId(Integer meetupId, Integer userId);
    List<MeetupParticipant> findByUserUserIdAndStatus(Integer userId, String status);
    void deleteByMeetupMeetupId(Integer meetupId);
    
    // Новые методы для фильтрации
    List<MeetupParticipant> findByUserUserIdAndStatusIn(Integer userId, List<String> statuses);
    List<MeetupParticipant> findByUserUserIdAndStatusAndMeetupStatusIn(Integer userId, String participantStatus, List<String> meetupStatuses);
    
    // Метод для поиска участников встречи по статусу
    List<MeetupParticipant> findByMeetupMeetupIdAndStatus(Integer meetupId, String status);
} 