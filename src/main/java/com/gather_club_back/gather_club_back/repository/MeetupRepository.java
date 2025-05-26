package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Integer> {
} 