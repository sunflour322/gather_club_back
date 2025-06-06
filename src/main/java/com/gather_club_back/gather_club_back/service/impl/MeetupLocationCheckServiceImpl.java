package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Meetup;
import com.gather_club_back.gather_club_back.entity.MeetupParticipant;
import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.entity.UserLocation;
import com.gather_club_back.gather_club_back.model.LocationCheckResult;
import com.gather_club_back.gather_club_back.model.UserLocationRequest;
import com.gather_club_back.gather_club_back.repository.MeetupParticipantRepository;
import com.gather_club_back.gather_club_back.repository.MeetupRepository;
import com.gather_club_back.gather_club_back.repository.UserLocationRepository;
import com.gather_club_back.gather_club_back.service.MeetupLocationCheckService;
import com.gather_club_back.gather_club_back.service.UserCurrencyService;
import com.gather_club_back.gather_club_back.service.UserLocationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetupLocationCheckServiceImpl implements MeetupLocationCheckService {

    private final MeetupRepository meetupRepository;
    private final MeetupParticipantRepository participantRepository;
    private final UserLocationRepository userLocationRepository;
    private final UserLocationService userLocationService;
    private final UserCurrencyService userCurrencyService;
    
    @Value("${app.meetup.reward.amount:50}")
    private Integer meetupRewardAmount;
    
    @Value("${app.meetup.location.radius:100}")
    private Double meetupLocationRadius;
    
    @Value("${app.meetup.participants.radius:10}")
    private Double participantsRadius;

    @Override
    @Transactional
    public LocationCheckResult checkLocationAndReward(Integer meetupId, Integer userId, UserLocationRequest location) {
        // Сохраняем текущее местоположение пользователя
        userLocationService.updateLocation(userId, location);
        
        LocationCheckResult result = new LocationCheckResult();
        
        // Проверяем, была ли уже начислена валюта за эту встречу
        if (userCurrencyService.hasTransactionForReference(userId, "meetup_attendance", meetupId)) {
            return result
                    .setSuccess(false)
                    .setAlreadyRewarded(true)
                    .setMessage("Валюта за эту встречу уже была начислена");
        }
        
        // Проверяем, находится ли пользователь рядом с местом встречи
        boolean isNearMeetup = isUserNearMeetupLocation(meetupId, userId, location, meetupLocationRadius);
        result.setNearMeetup(isNearMeetup);
        
        if (!isNearMeetup) {
            return result
                    .setSuccess(false)
                    .setMessage("Вы находитесь слишком далеко от места встречи");
        }
        
        // Проверяем, находятся ли все участники рядом друг с другом
        boolean areParticipantsNear = areParticipantsNearEachOther(meetupId, participantsRadius);
        result.setNearParticipants(areParticipantsNear);
        
        if (!areParticipantsNear) {
            return result
                    .setSuccess(false)
                    .setMessage("Не все участники находятся рядом друг с другом");
        }
        
        // Если все условия выполнены, начисляем валюту
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));
        
        String description = "Посещение встречи: " + meetup.getName();
        Integer newBalance = userCurrencyService.addCurrency(
                userId, 
                meetupRewardAmount, 
                description, 
                "meetup_attendance", 
                meetupId
        );
        
        return result
                .setSuccess(true)
                .setRewardAmount(meetupRewardAmount)
                .setNewBalance(newBalance)
                .setMessage("Валюта успешно начислена");
    }

    @Override
    public boolean isUserNearMeetupLocation(Integer meetupId, Integer userId, UserLocationRequest location, double radiusInMeters) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));
        
        Place place = meetup.getPlace();
        
        if (place == null || place.getLatitude() == null || place.getLongitude() == null) {
            log.warn("У встречи {} отсутствуют координаты места", meetupId);
            return false;
        }
        
        // Вычисляем расстояние между пользователем и местом встречи
        double distance = calculateDistance(
                location.getLatitude(), 
                location.getLongitude(), 
                place.getLatitude(), 
                place.getLongitude()
        );
        
        log.info("Расстояние между пользователем {} и местом встречи {}: {} метров (максимум: {})",
                userId, meetupId, distance, radiusInMeters);
        
        return distance <= radiusInMeters;
    }

    @Override
    public boolean areParticipantsNearEachOther(Integer meetupId, double radiusInMeters) {
        // Получаем всех участников встречи со статусом "accepted"
        List<MeetupParticipant> participants = participantRepository.findByMeetupMeetupIdAndStatus(meetupId, "accepted");
        
        if (participants.size() <= 1) {
            log.info("У встречи {} меньше двух участников", meetupId);
            return true; // Если участник один или их нет, считаем условие выполненным
        }
        
        // Получаем последние местоположения всех участников
        List<UserLocation> locations = participants.stream()
                .map(participant -> userLocationRepository.findFirstByUserUserIdOrderByTimestampDesc(participant.getUser().getUserId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
        
        if (locations.size() != participants.size()) {
            log.warn("Не все участники встречи {} имеют данные о местоположении", meetupId);
            return false;
        }
        
        // Проверяем, что каждый участник находится в пределах указанного радиуса от каждого другого участника
        for (int i = 0; i < locations.size(); i++) {
            UserLocation location1 = locations.get(i);
            
            for (int j = i + 1; j < locations.size(); j++) {
                UserLocation location2 = locations.get(j);
                
                double distance = calculateDistance(
                        location1.getLatitude(),
                        location1.getLongitude(),
                        location2.getLatitude(),
                        location2.getLongitude()
                );
                
                if (distance > radiusInMeters) {
                    log.info("Участники {} и {} находятся на расстоянии {} метров друг от друга (максимум: {})",
                            location1.getUser().getUserId(), location2.getUser().getUserId(), distance, radiusInMeters);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Вычисляет расстояние между двумя точками на земной поверхности по формуле гаверсинусов
     * 
     * @param lat1 Широта первой точки
     * @param lon1 Долгота первой точки
     * @param lat2 Широта второй точки
     * @param lon2 Долгота второй точки
     * @return Расстояние в метрах
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Радиус Земли в метрах
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
} 