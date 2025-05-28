package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ChatMessageRequest;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import com.gather_club_back.gather_club_back.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest messageRequest) {
        ChatMessageResponse response = chatService.saveAndProcessMessage(messageRequest);
        
        // Отправляем сообщение всем подписчикам чата
        messagingTemplate.convertAndSend(
            "/topic/chat." + messageRequest.getChatId(),
            response
        );
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload ChatMessageRequest messageRequest) {
        chatService.markMessageAsRead(messageRequest.getChatId(), messageRequest.getSenderId());
        
        // Уведомляем о прочтении сообщения
        messagingTemplate.convertAndSend(
            "/topic/chat." + messageRequest.getChatId() + ".read",
            messageRequest.getSenderId()
        );
    }

    @MessageMapping("/chat.typing")
    public void notifyTyping(@Payload ChatMessageRequest messageRequest) {
        // Уведомляем о наборе сообщения
        messagingTemplate.convertAndSend(
            "/topic/chat." + messageRequest.getChatId() + ".typing",
            messageRequest.getSenderId()
        );
    }
} 