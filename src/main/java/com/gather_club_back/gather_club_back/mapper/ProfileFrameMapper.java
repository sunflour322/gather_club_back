package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.ProfileFrame;
import com.gather_club_back.gather_club_back.entity.UserProfileFrame;
import com.gather_club_back.gather_club_back.model.ProfileFrameResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProfileFrameMapper {
    
    public ProfileFrameResponse toModel(ProfileFrame frame) {
        return new ProfileFrameResponse()
                .setFrameId(frame.getFrameId())
                .setName(frame.getName())
                .setDescription(frame.getDescription())
                .setPrice(frame.getPrice())
                .setPreviewUrl(frame.getPreviewUrl())
                .setIsDefault(frame.getIsDefault())
                .setIsActive(frame.getIsActive())
                .setIsPurchased(false)
                .setIsCurrentlyActive(false);
    }
    
    public ProfileFrameResponse toModelWithUserInfo(ProfileFrame frame, boolean isPurchased, boolean isCurrentlyActive) {
        return toModel(frame)
                .setIsPurchased(isPurchased)
                .setIsCurrentlyActive(isCurrentlyActive);
    }
    
    public List<ProfileFrameResponse> toModelList(List<ProfileFrame> frames, List<UserProfileFrame> userFrames) {
        // Создаем Map для быстрого доступа к информации о покупке и активации
        Map<Integer, UserProfileFrame> userFramesMap = userFrames.stream()
                .collect(Collectors.toMap(
                        userFrame -> userFrame.getFrame().getFrameId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        
        return frames.stream()
                .map(frame -> {
                    UserProfileFrame userFrame = userFramesMap.get(frame.getFrameId());
                    boolean isPurchased = userFrame != null;
                    boolean isCurrentlyActive = isPurchased && userFrame.getIsActive();
                    
                    return toModelWithUserInfo(frame, isPurchased, isCurrentlyActive);
                })
                .collect(Collectors.toList());
    }
}
