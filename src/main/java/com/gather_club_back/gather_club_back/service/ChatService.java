package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.ChatRequest;
import com.gather_club_back.gather_club_back.model.ChatResponse;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import com.gather_club_back.gather_club_back.model.ChatParticipantResponse;
import com.gather_club_back.gather_club_back.model.ChatMessageRequest;

import java.util.List;

public interface ChatService {
    ChatMessageResponse saveAndProcessMessage(ChatMessageRequest request);
    void markMessageAsRead(Integer chatId, Integer userId);
    List<ChatMessageResponse> getChatHistory(Integer chatId, Integer limit, Integer offset);
    List<ChatMessageResponse> getUnreadMessages(Integer chatId, Integer userId);
    
    ChatResponse createChat(ChatRequest request);
    List<ChatResponse> getUserChats();
    ChatResponse getChatById(Integer chatId);
    List<ChatMessageResponse> getChatMessages(Integer chatId, int page, int size);
    List<ChatParticipantResponse> getChatParticipants(Integer chatId);
    ChatParticipantResponse addParticipant(Integer chatId, Integer userId);
    void removeParticipant(Integer chatId, Integer userId);
    void deleteChat(Integer chatId);
} 