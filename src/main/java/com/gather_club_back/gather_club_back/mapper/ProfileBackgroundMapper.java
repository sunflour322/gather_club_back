package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.ProfileBackground;
import com.gather_club_back.gather_club_back.entity.UserProfileBackground;
import com.gather_club_back.gather_club_back.model.ProfileBackgroundResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProfileBackgroundMapper {
    
    public ProfileBackgroundResponse toModel(ProfileBackground background) {
        return new ProfileBackgroundResponse()
                .setBackgroundId(background.getBackgroundId())
                .setName(background.getName())
                .setDescription(background.getDescription())
                .setPrice(background.getPrice())
                .setPreviewUrl(background.getPreviewUrl())
                .setIsDefault(background.getIsDefault())
                .setIsActive(background.getIsActive())
                .setIsPurchased(false)
                .setIsCurrentlyActive(false);
    }
    
    public ProfileBackgroundResponse toModelWithUserInfo(ProfileBackground background, boolean isPurchased, boolean isCurrentlyActive) {
        return toModel(background)
                .setIsPurchased(isPurchased)
                .setIsCurrentlyActive(isCurrentlyActive);
    }
    
    public List<ProfileBackgroundResponse> toModelList(List<ProfileBackground> backgrounds, List<UserProfileBackground> userBackgrounds) {
        // Создаем Map для быстрого доступа к информации о покупке и активации
        Map<Integer, UserProfileBackground> userBackgroundsMap = userBackgrounds.stream()
                .collect(Collectors.toMap(
                        userBackground -> userBackground.getBackground().getBackgroundId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        
        return backgrounds.stream()
                .map(background -> {
                    UserProfileBackground userBackground = userBackgroundsMap.get(background.getBackgroundId());
                    boolean isPurchased = userBackground != null;
                    boolean isCurrentlyActive = isPurchased && userBackground.getIsActive();
                    
                    return toModelWithUserInfo(background, isPurchased, isCurrentlyActive);
                })
                .collect(Collectors.toList());
    }
}
