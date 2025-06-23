package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.FriendshipResponse;
import com.gather_club_back.gather_club_back.service.FriendshipService;
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
@RequestMapping("/friendships")
@RequiredArgsConstructor
@Tag(name = "Дружба", description = "API для управления дружескими связями между пользователями")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Operation(summary = "Отправить запрос на дружбу", description = "Отправляет запрос на дружбу от одного пользователя другому")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос успешно отправлен", 
                    content = @Content(schema = @Schema(implementation = FriendshipResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/request/{userId}/{friendId}")
    public ResponseEntity<FriendshipResponse> sendFriendRequest(
            @Parameter(description = "ID отправителя запроса", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID получателя запроса", required = true) @PathVariable Integer friendId) {
        return ResponseEntity.ok(friendshipService.sendFriendRequest(userId, friendId));
    }

    @Operation(summary = "Принять запрос на дружбу", description = "Принимает входящий запрос на дружбу")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос успешно принят", 
                    content = @Content(schema = @Schema(implementation = FriendshipResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "404", description = "Пользователь или запрос не найдены")
    })
    @PostMapping("/{userId}/{friendshipId}/accept")
    public ResponseEntity<FriendshipResponse> acceptFriendRequest(
            @Parameter(description = "ID пользователя, принимающего запрос", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID запроса на дружбу", required = true) @PathVariable Integer friendshipId) {
        return ResponseEntity.ok(friendshipService.acceptFriendRequest(userId, friendshipId));
    }

    @Operation(summary = "Отклонить запрос на дружбу", description = "Отклоняет входящий запрос на дружбу")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос успешно отклонен", 
                    content = @Content(schema = @Schema(implementation = FriendshipResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "404", description = "Пользователь или запрос не найдены")
    })
    @PostMapping("/{userId}/{friendshipId}/reject")
    public ResponseEntity<FriendshipResponse> rejectFriendRequest(
            @Parameter(description = "ID пользователя, отклоняющего запрос", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID запроса на дружбу", required = true) @PathVariable Integer friendshipId) {
        return ResponseEntity.ok(friendshipService.rejectFriendRequest(userId, friendshipId));
    }

    @Operation(summary = "Получить список друзей", description = "Возвращает список всех друзей пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список друзей успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendshipResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<FriendshipResponse>> getAllFriends(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipService.getAllFriends(userId));
    }

    @Operation(summary = "Получить ожидающие запросы", description = "Возвращает список всех ожидающих запросов на дружбу пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список запросов успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendshipResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/pending")
    public ResponseEntity<List<FriendshipResponse>> getPendingRequests(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipService.getPendingRequests(userId));
    }

    @Operation(summary = "Получить все запросы", description = "Возвращает список всех запросов на дружбу пользователя (входящие и исходящие)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список запросов успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendshipResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<FriendshipResponse>> getAllRequests(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipService.getAllRequests(userId));
    }

    @Operation(summary = "Получить исходящие запросы", description = "Возвращает список исходящих запросов на дружбу пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список запросов успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendshipResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/outgoing")
    public ResponseEntity<List<FriendshipResponse>> getOutgoingRequests(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipService.getOutgoingRequests(userId));
    }

    @Operation(summary = "Получить входящие запросы", description = "Возвращает список входящих запросов на дружбу пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список запросов успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendshipResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/incoming")
    public ResponseEntity<List<FriendshipResponse>> getIncomingRequests(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipService.getIncomingRequests(userId));
    }

    @Operation(summary = "Получить статус дружбы", description = "Возвращает текущий статус дружбы между двумя пользователями")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус дружбы успешно получен", 
                    content = @Content(schema = @Schema(implementation = FriendshipResponse.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или отношения дружбы не существуют")
    })
    @GetMapping("/{userId}/status/{friendId}")
    public ResponseEntity<FriendshipResponse> getFriendshipStatus(
            @Parameter(description = "ID первого пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID второго пользователя", required = true) @PathVariable Integer friendId) {
        return ResponseEntity.ok(friendshipService.getFriendshipStatus(userId, friendId));
    }

    @Operation(summary = "Удалить друга", description = "Удаляет дружескую связь между двумя пользователями")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Дружба успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или дружба не существует")
    })
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(
            @Parameter(description = "ID пользователя, инициирующего удаление", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID друга для удаления", required = true) @PathVariable Integer friendId) {
        friendshipService.deleteFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }
}
