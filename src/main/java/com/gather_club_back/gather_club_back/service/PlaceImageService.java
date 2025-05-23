package com.gather_club_back.gather_club_back.service;


import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PlaceImageService {
    List<PlaceImageResponse> getPlaceImages(Integer placeId);
    PlaceImageResponse uploadPlaceImage(Integer placeId, MultipartFile imageFile, Integer userId) throws IOException;
    void rateImage(Integer imageId, boolean isLike);
}
