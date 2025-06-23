package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.LocationCheckResult;
import com.gather_club_back.gather_club_back.model.UserLocationRequest;
import com.gather_club_back.gather_club_back.service.MeetupLocationCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/meetups/{meetupId}/location-check")
@RequiredArgsConstructor
@Tag(name = "Проверка местоположения", description = "API для проверки местоположения пользователя на встрече и начисления вознаграждения")
public class MeetupLocationCheckController {

    private final MeetupLocationCheckService locationCheckService;

    @Operation(summary = "Проверить местоположение и начислить вознаграждение", 
              description = "Проверяет, находится ли пользователь в месте проведения встречи, и начисляет вознаграждение при успешной проверке")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Проверка выполнена", 
                    content = @Content(schema = @Schema(implementation = LocationCheckResult.class))),
        @ApiResponse(responseCode = "404", description = "Встреча или пользователь не найдены")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<LocationCheckResult> checkLocationAndReward(
            @Parameter(description = "ID встречи", required = true) @PathVariable Integer meetupId,
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Данные о местоположении пользователя", required = true) 
            @RequestBody UserLocationRequest locationRequest) {
        
        log.info("Получен запрос на проверку местоположения для пользователя {} на встрече {}", userId, meetupId);
        
        LocationCheckResult result = locationCheckService.checkLocationAndReward(meetupId, userId, locationRequest);
        
        if (result.isSuccess()) {
            log.info("Успешно начислена валюта пользователю {} за посещение встречи {}", userId, meetupId);
        } else {
            log.info("Не удалось начислить валюту пользователю {} за встречу {}: {}", 
                    userId, meetupId, result.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
}
