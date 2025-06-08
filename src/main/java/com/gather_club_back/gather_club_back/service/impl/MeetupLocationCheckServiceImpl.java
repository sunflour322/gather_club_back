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
    
    // Добавляем логирование координат
    log.info("Координаты пользователя {}: lat={}, lon={}", 
            userId, location.getLatitude(), location.getLongitude());
    log.info("Координаты места встречи {}: lat={}, lon={}", 
            meetupId, place.getLatitude(), place.getLongitude());
    
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
        // Получаем встречу для доступа к организатору
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));
        
        // Получаем всех участников встречи со статусом "accepted"
        List<MeetupParticipant> participants = participantRepository.findByMeetupMeetupIdAndStatus(meetupId, "accepted");
        
        log.warn("Проверка расстояния между участниками встречи {}, участников с accepted: {}", 
                meetupId, participants.size());
        
        // Если нет участников со статусом accepted, проверять нечего
        if (participants.isEmpty()) {
            log.info("У встречи {} нет участников со статусом accepted", meetupId);
            return true;
        }
        
        // Создаем список всех пользователей, включая организатора
        List<Integer> allUserIds = new java.util.ArrayList<>();
        
        // Добавляем организатора
        allUserIds.add(meetup.getCreator().getUserId());
        
        // Добавляем участников
        participants.forEach(p -> allUserIds.add(p.getUser().getUserId()));
        
        log.warn("Всего участников встречи {} (включая организатора): {}", meetupId, allUserIds.size());
        
        // Получаем последние местоположения всех пользователей
        List<UserLocation> locations = allUserIds.stream()
                .map(userId -> userLocationRepository.findFirstByUserUserIdOrderByTimestampDesc(userId))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
        
        log.warn("Получены данные о местоположении для {} из {} участников встречи {}", 
                locations.size(), allUserIds.size(), meetupId);
        
        if (locations.size() != allUserIds.size()) {
            log.warn("Не все участники встречи {} имеют данные о местоположении: {}/{}", 
                    meetupId, locations.size(), allUserIds.size());
            return false;
        }
        
        // Если только один участник, проверять расстояние не нужно
        if (locations.size() < 2) {
            log.warn("У встречи {} только один участник с данными о местоположении", meetupId);
            return true;
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
                
                // Принудительно выводим в лог информацию о расстоянии между каждой парой участников
                log.warn("Расстояние между участниками {} и {}: {} метров (максимум: {})",
                        location1.getUser().getUserId(), location2.getUser().getUserId(), distance, radiusInMeters);
                
                if (distance > radiusInMeters) {
                    log.error("Превышено максимальное расстояние между участниками {} и {}: {} > {}", 
                            location1.getUser().getUserId(), location2.getUser().getUserId(), 
                            distance, radiusInMeters);
                    return false;
                }
            }
        }
        
        log.info("Все участники встречи {} находятся в пределах {} метров друг от друга", meetupId, radiusInMeters);
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
        
        // Преобразуем координаты из градусов в радианы
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // Разница координат
        double latDistance = lat2Rad - lat1Rad;
        double lonDistance = lon2Rad - lon1Rad;
        
        // Формула гаверсинусов
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Расстояние в метрах
        double distance = R * c;
        
        // Округляем до 2 знаков после запятой для логирования
        return Math.round(distance * 100) / 100.0;
    }
}
