package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.FriendshipResponse;
import java.util.List;

public interface FriendshipService {
    FriendshipResponse sendFriendRequest(Integer currentUserId, Integer friendId);
    FriendshipResponse acceptFriendRequest(Integer currentUserId, Integer friendshipId);
    FriendshipResponse rejectFriendRequest(Integer currentUserId, Integer friendshipId);
    List<FriendshipResponse> getAllFriends(Integer userId);
    List<FriendshipResponse> getPendingRequests(Integer userId);
} 