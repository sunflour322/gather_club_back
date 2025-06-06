package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.LocationCheckResult;
import com.gather_club_back.gather_club_back.model.UserLocationRequest;
import com.gather_club_back.gather_club_back.service.MeetupLocationCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/meetups/{meetupId}/location-check")
@RequiredArgsConstructor
public class MeetupLocationCheckController {

    private final MeetupLocationCheckService locationCheckService;

    @PostMapping("/{userId}")
    public ResponseEntity<LocationCheckResult> checkLocationAndReward(
            @PathVariable Integer meetupId,
            @PathVariable Integer userId,
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