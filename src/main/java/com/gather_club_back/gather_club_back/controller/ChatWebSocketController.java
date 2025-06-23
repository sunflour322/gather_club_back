package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ChatMessageRequest;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import com.gather_club_back.gather_club_back.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Tag(name = "Чат WebSocket", description = "API для обмена сообщениями в реальном времени через WebSocket")
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @Operation(summary = "Отправить сообщение", description = "Отправляет новое сообщение в чат и уведомляет всех участников")
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Parameter(description = "Данные сообщения", required = true) @Payload ChatMessageRequest messageRequest) {
        ChatMessageResponse response = chatService.saveAndProcessMessage(messageRequest);
        
        // Отправляем сообщение всем подписчикам чата
        messagingTemplate.convertAndSend(
            "/topic/chat." + messageRequest.getChatId(),
            response
        );
    }

    @Operation(summary = "Отметить сообщения как прочитанные", description = "Отмечает сообщения в чате как прочитанные указанным пользователем")
    @MessageMapping("/chat.read")
    public void markAsRead(
            @Parameter(description = "Данные о прочтении", required = true) @Payload ChatMessageRequest messageRequest) {
        chatService.markMessageAsRead(messageRequest.getChatId(), messageRequest.getSenderId());
        
        // Уведомляем о прочтении сообщения
        messagingTemplate.convertAndSend(
            "/topic/chat." + messageRequest.getChatId() + ".read",
            messageRequest.getSenderId()
        );
    }

    @Operation(summary = "Уведомить о наборе сообщения", description = "Отправляет уведомление о том, что пользователь печатает сообщение")
    @MessageMapping("/chat.typing")
    public void notifyTyping(
            @Parameter(description = "Данные о наборе сообщения", required = true) @Payload ChatMessageRequest messageRequest) {
        // Уведомляем о наборе сообщения
        messagingTemplate.convertAndSend(
            "/topic/chat." + messageRequest.getChatId() + ".typing",
            messageRequest.getSenderId()
        );
    }
}
