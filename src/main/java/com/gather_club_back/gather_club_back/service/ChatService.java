package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.ChatMessageRequest;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import java.util.List;

public interface ChatService {
    ChatMessageResponse saveAndProcessMessage(ChatMessageRequest request);
    void markMessageAsRead(Integer chatId, Integer userId);
    List<ChatMessageResponse> getChatHistory(Integer chatId, Integer limit, Integer offset);
    List<ChatMessageResponse> getUnreadMessages(Integer chatId, Integer userId);
} 