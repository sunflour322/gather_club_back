package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ChatRequest;
import com.gather_club_back.gather_club_back.model.ChatResponse;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import com.gather_club_back.gather_club_back.model.ChatParticipantResponse;
import com.gather_club_back.gather_club_back.model.ChatParticipantRequest;
import com.gather_club_back.gather_club_back.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "Чаты", description = "API для управления чатами и сообщениями")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Создать чат", description = "Создает новый чат на основе предоставленных данных")
    @ApiResponse(responseCode = "200", description = "Чат успешно создан", 
                content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    @PostMapping
    public ResponseEntity<ChatResponse> createChat(
            @Parameter(description = "Данные для создания чата", required = true) @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.createChat(request));
    }

    @Operation(summary = "Получить чаты текущего пользователя", description = "Возвращает список всех чатов текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Список чатов успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatResponse.class))))
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getUserChats() {
        return ResponseEntity.ok(chatService.getUserChats());
    }

    @Operation(summary = "Получить чаты пользователя", description = "Возвращает список всех чатов указанного пользователя")
    @ApiResponse(responseCode = "200", description = "Список чатов успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatResponse.class))))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatResponse>> getUserChats(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(chatService.getUserChats());
    }

    @Operation(summary = "Получить чат по ID", description = "Возвращает информацию о чате по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно найден", 
                    content = @Content(schema = @Schema(implementation = ChatResponse.class))),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(
            @Parameter(description = "ID чата", required = true) @PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.getChatById(chatId));
    }

    @Operation(summary = "Получить чат встречи", description = "Возвращает информацию о чате, связанном с указанной встречей")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно найден", 
                    content = @Content(schema = @Schema(implementation = ChatResponse.class))),
        @ApiResponse(responseCode = "404", description = "Чат не найден или встреча не имеет чата")
    })
    @GetMapping("/meetup/{meetupId}")
    public ResponseEntity<ChatResponse> getChatByMeetupId(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId) {
        return ResponseEntity.ok(chatService.getChatByMeetupId(meetupId));
    }

    @Operation(summary = "Получить сообщения чата", description = "Возвращает список сообщений указанного чата с пагинацией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список сообщений успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatMessageResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(
            @Parameter(description = "ID чата", required = true) @PathVariable Integer chatId,
            @Parameter(description = "Номер страницы (начиная с 0)", required = false) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", required = false) @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getChatMessages(chatId, page, size));
    }

    @Operation(summary = "Получить участников чата", description = "Возвращает список всех участников указанного чата")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список участников успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatParticipantResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/{chatId}/participants")
    public ResponseEntity<List<ChatParticipantResponse>> getChatParticipants(
            @Parameter(description = "ID чата", required = true) @PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.getChatParticipants(chatId));
    }

    @Operation(summary = "Добавить участника в чат", description = "Добавляет указанного пользователя в чат")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Участник успешно добавлен", 
                    content = @Content(schema = @Schema(implementation = ChatParticipantResponse.class))),
        @ApiResponse(responseCode = "404", description = "Чат или пользователь не найдены")
    })
    @PostMapping("/{chatId}/participants")
    public ResponseEntity<ChatParticipantResponse> addParticipant(
            @Parameter(description = "ID чата", required = true) @PathVariable Integer chatId,
            @Parameter(description = "ID пользователя", required = true) @RequestParam Integer userId) {
        return ResponseEntity.ok(chatService.addParticipant(chatId, userId));
    }

    @Operation(summary = "Удалить участника из чата", description = "Удаляет указанного пользователя из чата")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Участник успешно удален"),
        @ApiResponse(responseCode = "404", description = "Чат, пользователь или участие не найдены")
    })
    @DeleteMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<Void> removeParticipant(
            @Parameter(description = "ID чата", required = true) @PathVariable Integer chatId,
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        chatService.removeParticipant(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить чат", description = "Удаляет указанный чат и все связанные с ним данные")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удален"),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @Parameter(description = "ID чата", required = true) @PathVariable Integer chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить информацию об участниках чата", description = "Возвращает расширенную информацию обо всех участниках указанного чата")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Информация об участниках успешно получена", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatParticipantRequest.class)))),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/{chatId}/participants/info")
    public ResponseEntity<List<ChatParticipantRequest>> getChatParticipantsInfo(
            @Parameter(description = "ID чата", required = true) @PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.getChatParticipantsInfo(chatId));
    }
}
