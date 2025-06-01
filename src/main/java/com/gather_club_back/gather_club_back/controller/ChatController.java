package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ChatRequest;
import com.gather_club_back.gather_club_back.model.ChatResponse;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import com.gather_club_back.gather_club_back.model.ChatParticipantResponse;
import com.gather_club_back.gather_club_back.model.ChatParticipantRequest;
import com.gather_club_back.gather_club_back.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> createChat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.createChat(request));
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> getUserChats() {
        return ResponseEntity.ok(chatService.getUserChats());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatResponse>> getUserChats(@PathVariable Integer userId) {
        return ResponseEntity.ok(chatService.getUserChats());
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(@PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.getChatById(chatId));
    }

    @GetMapping("/meetup/{meetupId}")
    public ResponseEntity<ChatResponse> getChatByMeetupId(@PathVariable Integer meetupId) {
        return ResponseEntity.ok(chatService.getChatByMeetupId(meetupId));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(
            @PathVariable Integer chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getChatMessages(chatId, page, size));
    }

    @GetMapping("/{chatId}/participants")
    public ResponseEntity<List<ChatParticipantResponse>> getChatParticipants(@PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.getChatParticipants(chatId));
    }

    @PostMapping("/{chatId}/participants")
    public ResponseEntity<ChatParticipantResponse> addParticipant(
            @PathVariable Integer chatId,
            @RequestParam Integer userId) {
        return ResponseEntity.ok(chatService.addParticipant(chatId, userId));
    }

    @DeleteMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Integer chatId,
            @PathVariable Integer userId) {
        chatService.removeParticipant(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Integer chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}/participants/info")
    public ResponseEntity<List<ChatParticipantRequest>> getChatParticipantsInfo(@PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.getChatParticipantsInfo(chatId));
    }
} 