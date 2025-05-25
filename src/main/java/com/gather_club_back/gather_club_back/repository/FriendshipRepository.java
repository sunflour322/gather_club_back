package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.Friendship;
import com.gather_club_back.gather_club_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.user1 = :user AND f.user2 = :friend) OR " +
           "(f.user1 = :friend AND f.user2 = :user)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("user") User user, @Param("friend") User friend);
    
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = 'accepted'")
    List<Friendship> findAllAcceptedFriendships(@Param("user") User user);
    
    @Query("SELECT f FROM Friendship f WHERE f.user2 = :user AND f.status = 'pending'")
    List<Friendship> findAllPendingFriendRequests(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = 'pending'")
    List<Friendship> findAllRequests(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE f.user1 = :user AND f.status = 'pending'")
    List<Friendship> findOutgoingRequests(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE f.user2 = :user AND f.status = 'pending'")
    List<Friendship> findIncomingRequests(@Param("user") User user);
} 