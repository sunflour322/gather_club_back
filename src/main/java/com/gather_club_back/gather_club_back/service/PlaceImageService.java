package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.PlaceImageRequest;
import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PlaceImageService {
    PlaceImageResponse addImage(Integer userId, PlaceImageRequest request);
    List<PlaceImageResponse> getPlaceImages(Integer placeId);
    void approveImage(Integer imageId);
    void rejectImage(Integer imageId);
    void addLike(Integer userId, Integer imageId);
    void addDislike(Integer userId, Integer imageId);
    void removeLike(Integer userId, Integer imageId);
    void removeDislike(Integer userId, Integer imageId);
    PlaceImageResponse uploadPlaceImage(Integer placeId, MultipartFile imageFile, Integer userId) throws IOException;
    void rateImage(Integer imageId, boolean isLike);
    String getMainPlaceImageUrl(Integer placeId);
    List<PlaceImageResponse> getPendingImages();
}
