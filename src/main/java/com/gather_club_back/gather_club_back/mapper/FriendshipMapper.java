package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.Friendship;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.model.FriendshipResponse;
import org.springframework.stereotype.Component;

@Component
public class FriendshipMapper {
    
    public FriendshipResponse toModel(Friendship entity) {
        if (entity == null) {
            return null;
        }
        
        return new FriendshipResponse()
                .setFriendshipId(entity.getFriendshipId())
                .setUser1Id(entity.getUser1().getUserId())
                .setUser2Id(entity.getUser2().getUserId())
                .setStatus(entity.getStatus())
                .setCreatedAt(entity.getCreatedAt())
                .setUpdatedAt(entity.getUpdatedAt());
    }
    
    public Friendship toEntity(FriendshipResponse model, User user1, User user2) {
        if (model == null) {
            return null;
        }
        
        return new Friendship()
                .setFriendshipId(model.getFriendshipId())
                .setUser1(user1)
                .setUser2(user2)
                .setStatus(model.getStatus())
                .setCreatedAt(model.getCreatedAt())
                .setUpdatedAt(model.getUpdatedAt());
    }
} 