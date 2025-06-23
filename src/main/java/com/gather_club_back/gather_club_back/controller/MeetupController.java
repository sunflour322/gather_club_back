package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.MeetupParticipantResponse;
import com.gather_club_back.gather_club_back.model.MeetupRequest;
import com.gather_club_back.gather_club_back.model.MeetupResponse;
import com.gather_club_back.gather_club_back.service.MeetupService;
import com.gather_club_back.gather_club_back.service.UserService;
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
@RequestMapping("/meetups")
@RequiredArgsConstructor
@Tag(name = "Встречи", description = "API для управления встречами пользователей")
public class MeetupController {

    private final MeetupService meetupService;
    private final UserService userService;

    @Operation(summary = "Создать встречу", description = "Создает новую встречу на основе предоставленных данных")
    @ApiResponse(responseCode = "200", description = "Встреча успешно создана", 
                content = @Content(schema = @Schema(implementation = MeetupResponse.class)))
    @PostMapping
    public ResponseEntity<MeetupResponse> createMeetup(
            @Parameter(description = "Данные для создания встречи", required = true) 
            @RequestBody MeetupRequest request) {
        return ResponseEntity.ok(meetupService.createMeetup(request));
    }

    @Operation(summary = "Получить встречу по ID", description = "Возвращает информацию о встрече по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Встреча успешно найдена", 
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
        @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @GetMapping("/{meetupId}")
    public ResponseEntity<MeetupResponse> getMeetup(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId) {
        return ResponseEntity.ok(meetupService.getMeetup(meetupId));
    }

    @Operation(summary = "Получить встречи пользователя", description = "Возвращает список всех встреч пользователя по указанному ID")
    @ApiResponse(responseCode = "200", description = "Список встреч успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupResponse.class))))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MeetupResponse>> getUserMeetups(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(meetupService.getUserMeetups(userId));
    }

    @Operation(summary = "Пригласить участников", description = "Приглашает указанных пользователей на встречу")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Приглашения успешно отправлены"),
        @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @PostMapping("/{meetupId}/invite")
    public ResponseEntity<Void> inviteParticipants(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId,
            @Parameter(description = "Список ID пользователей для приглашения", required = true) 
            @RequestBody List<Integer> userIds) {
        meetupService.inviteParticipants(meetupId, userIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновить статус участника", description = "Обновляет статус участия пользователя во встрече")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно обновлен", 
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
        @ApiResponse(responseCode = "404", description = "Встреча или пользователь не найдены")
    })
    @PutMapping("/{meetupId}/participants/{userId}")
    public ResponseEntity<MeetupResponse> updateParticipantStatus(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId,
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Новый статус участия (ACCEPTED, DECLINED, PENDING)", required = true) 
            @RequestParam String status) {
        return ResponseEntity.ok(meetupService.updateParticipantStatus(meetupId, userId, status));
    }

    @Operation(summary = "Получить активные встречи", description = "Возвращает список активных встреч текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Список активных встреч успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupResponse.class))))
    @GetMapping("/active")
    public ResponseEntity<List<MeetupResponse>> getActiveMeetups() {
        Integer userId = userService.getUserId();
        return ResponseEntity.ok(meetupService.getActiveMeetups(userId));
    }

    @Operation(summary = "Получить архивные встречи", description = "Возвращает список архивных встреч текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Список архивных встреч успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupResponse.class))))
    @GetMapping("/archived")
    public ResponseEntity<List<MeetupResponse>> getArchivedMeetups() {
        Integer userId = userService.getUserId();
        return ResponseEntity.ok(meetupService.getArchivedMeetups(userId));
    }

    @Operation(summary = "Получить приглашения на встречи", description = "Возвращает список встреч, на которые приглашен текущий пользователь")
    @ApiResponse(responseCode = "200", description = "Список приглашений успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupResponse.class))))
    @GetMapping("/invited")
    public ResponseEntity<List<MeetupResponse>> getInvitedMeetups() {
        Integer userId = userService.getUserId();
        return ResponseEntity.ok(meetupService.getInvitedMeetups(userId));
    }

    @Operation(summary = "Принять приглашение на встречу", description = "Принимает приглашение на встречу для текущего пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Приглашение успешно принято", 
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
        @ApiResponse(responseCode = "404", description = "Встреча не найдена или пользователь не приглашен")
    })
    @PostMapping("/{meetupId}/accept")
    public ResponseEntity<MeetupResponse> acceptInvitation(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId) {
        Integer userId = userService.getUserId();
        return ResponseEntity.ok(meetupService.acceptInvitation(meetupId, userId));
    }

    @Operation(summary = "Отклонить приглашение на встречу", description = "Отклоняет приглашение на встречу для текущего пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Приглашение успешно отклонено", 
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
        @ApiResponse(responseCode = "404", description = "Встреча не найдена или пользователь не приглашен")
    })
    @PostMapping("/{meetupId}/decline")
    public ResponseEntity<MeetupResponse> declineInvitation(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId) {
        Integer userId = userService.getUserId();
        return ResponseEntity.ok(meetupService.declineInvitation(meetupId, userId));
    }

    @Operation(summary = "Получить завершенные встречи пользователя", description = "Возвращает список завершенных встреч указанного пользователя")
    @ApiResponse(responseCode = "200", description = "Список завершенных встреч успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupResponse.class))))
    @GetMapping("/completed/{userId}")
    public ResponseEntity<List<MeetupResponse>> getCompletedMeetups(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(meetupService.getCompletedMeetups(userId));
    }

    @Operation(summary = "Получить ожидающие встречи пользователя", description = "Возвращает список ожидающих встреч указанного пользователя")
    @ApiResponse(responseCode = "200", description = "Список ожидающих встреч успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupResponse.class))))
    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<MeetupResponse>> getPendingMeetups(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(meetupService.getPendingMeetups(userId));
    }

    @Operation(summary = "Получить созданные и принятые встречи пользователя", 
              description = "Возвращает список встреч, созданных пользователем или на которые он согласился")
    @ApiResponse(responseCode = "200", description = "Список встреч успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupResponse.class))))
    @GetMapping("/owned-and-accepted/{userId}")
    public ResponseEntity<List<MeetupResponse>> getOwnedAndAcceptedMeetups(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(meetupService.getOwnedAndAcceptedMeetups(userId));
    }
    
    @Operation(summary = "Отменить встречу", description = "Отменяет встречу (доступно только организатору)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Встреча успешно отменена", 
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для отмены встречи"),
        @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @PostMapping("/{meetupId}/cancel")
    public ResponseEntity<MeetupResponse> cancelMeetup(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId) {
        Integer userId = userService.getUserId();
        return ResponseEntity.ok(meetupService.cancelMeetup(meetupId, userId));
    }
    
    @Operation(summary = "Обновить встречу", description = "Обновляет информацию о встрече (доступно только организатору)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Встреча успешно обновлена", 
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для обновления встречи"),
        @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @PutMapping("/{meetupId}")
    public ResponseEntity<MeetupResponse> updateMeetup(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId,
            @Parameter(description = "Обновленные данные встречи", required = true) @RequestBody MeetupRequest request) {
        Integer userId = userService.getUserId();
        return ResponseEntity.ok(meetupService.updateMeetup(meetupId, userId, request));
    }
    
    @Operation(summary = "Получить участников встречи", description = "Возвращает список участников указанной встречи")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список участников успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MeetupParticipantResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Встреча не найдена")
    })
    @GetMapping("/{meetupId}/participants")
    public ResponseEntity<List<MeetupParticipantResponse>> getMeetupParticipants(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId) {
        return ResponseEntity.ok(meetupService.getMeetupParticipants(meetupId));
    }
    
    @Operation(summary = "Удалить участника встречи", description = "Удаляет указанного пользователя из списка участников встречи")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Участник успешно удален", 
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления участника"),
        @ApiResponse(responseCode = "404", description = "Встреча или пользователь не найдены")
    })
    @DeleteMapping("/{meetupId}/participants/{userId}")
    public ResponseEntity<MeetupResponse> removeParticipant(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId,
            @Parameter(description = "ID пользователя для удаления", required = true) @PathVariable Integer userId) {
        Integer currentUserId = userService.getUserId();
        return ResponseEntity.ok(meetupService.removeParticipant(meetupId, userId));
    }
}
