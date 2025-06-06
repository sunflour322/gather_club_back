package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.LocationCheckResult;
import com.gather_club_back.gather_club_back.model.UserLocationRequest;

public interface MeetupLocationCheckService {
    
    /**
     * Проверяет местоположение пользователя относительно встречи и других участников
     * и начисляет валюту, если выполнены все условия
     * 
     * @param meetupId ID встречи
     * @param userId ID пользователя
     * @param location Текущее местоположение пользователя
     * @return Результат проверки с информацией о начислении валюты
     */
    LocationCheckResult checkLocationAndReward(Integer meetupId, Integer userId, UserLocationRequest location);
    
    /**
     * Проверяет, находится ли пользователь в радиусе от места встречи
     * 
     * @param meetupId ID встречи
     * @param userId ID пользователя
     * @param location Текущее местоположение пользователя
     * @param radiusInMeters Радиус в метрах
     * @return true, если пользователь находится в указанном радиусе от места встречи
     */
    boolean isUserNearMeetupLocation(Integer meetupId, Integer userId, UserLocationRequest location, double radiusInMeters);
    
    /**
     * Проверяет, находятся ли участники встречи в указанном радиусе друг от друга
     * 
     * @param meetupId ID встречи
     * @param radiusInMeters Радиус в метрах
     * @return true, если все участники находятся в указанном радиусе друг от друга
     */
    boolean areParticipantsNearEachOther(Integer meetupId, double radiusInMeters);
} 