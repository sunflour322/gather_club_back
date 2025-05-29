package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.Message;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public ChatMessageResponse toModel(Message entity) {
        if (entity == null) {
            return null;
        }

        ChatMessageResponse response = new ChatMessageResponse();
        response.setMessageId(entity.getMessageId());
        response.setChatId(entity.getChat().getChatId());
        response.setSenderId(entity.getSender().getUserId());
        response.setSenderName(entity.getSender().getUsername());
        response.setSenderAvatar(entity.getSender().getAvatarUrl());
        response.setContent(entity.getContent());
        response.setSentAt(entity.getSentAt());
        response.setReadAt(entity.getReadAt());
        response.setIsSystem(entity.getIsSystem());
        
        if (entity.getReplyTo() != null) {
            response.setReplyToId(entity.getReplyTo().getMessageId());
        }

        return response;
    }
} 