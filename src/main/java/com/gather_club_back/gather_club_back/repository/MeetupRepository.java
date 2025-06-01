package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Integer> {
    List<Meetup> findByCreatorUserId(Integer userId);
    List<Meetup> findByCreatorUserIdAndStatus(Integer userId, String status);

    @Query("""
            SELECT DISTINCT m FROM Meetup m
            WHERE m.status = 'planned' AND
            (m.creator.userId = :userId OR
            EXISTS (
                SELECT 1 FROM MeetupParticipant mp
                WHERE mp.meetup = m AND mp.user.userId = :userId AND mp.status = 'accepted'
            ))
            """)
    List<Meetup> findActiveMeetups(@Param("userId") Integer userId);

    @Query("""
            SELECT DISTINCT m FROM Meetup m
            WHERE m.status = 'completed' AND
            (m.creator.userId = :userId OR
            EXISTS (
                SELECT 1 FROM MeetupParticipant mp
                WHERE mp.meetup = m AND mp.user.userId = :userId AND mp.status = 'accepted'
            ))
            """)
    List<Meetup> findArchivedMeetups(@Param("userId") Integer userId);

    @Query("""
            SELECT DISTINCT m FROM Meetup m
            WHERE EXISTS (
                SELECT 1 FROM MeetupParticipant mp
                WHERE mp.meetup = m AND mp.user.userId = :userId AND mp.status = 'invited'
            )
            """)
    List<Meetup> findInvitedMeetups(@Param("userId") Integer userId);
} 