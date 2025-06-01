package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.entity.PlaceImage;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.PlaceImageMapper;
import com.gather_club_back.gather_club_back.model.PlaceImageRequest;
import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import com.gather_club_back.gather_club_back.repository.PlaceImageRepository;
import com.gather_club_back.gather_club_back.repository.PlaceRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.PlaceImageService;
import com.gather_club_back.gather_club_back.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceImageServiceImpl implements PlaceImageService {

    private final PlaceImageRepository placeImageRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceImageMapper placeImageMapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public PlaceImageResponse addImage(Integer userId, PlaceImageRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Место не найдено"));

        PlaceImage image = new PlaceImage()
                .setPlace(place)
                .setImageUrl(request.getImageUrl())
                .setUploadedBy(user)
                .setUploadedAt(LocalDateTime.now())
                .setIsApproved(false)
                .setLikes(0)
                .setDislikes(0);

        return placeImageMapper.toModel(placeImageRepository.save(image));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceImageResponse> getPlaceImages(Integer placeId) {
        List<PlaceImage> images = placeImageRepository.findByPlacePlaceId(placeId);
        return images.stream()
                .map(placeImageMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveImage(Integer imageId) {
        PlaceImage image = placeImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));
        image.setIsApproved(true);
        placeImageRepository.save(image);
    }

    @Override
    @Transactional
    public void rejectImage(Integer imageId) {
        placeImageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void addLike(Integer userId, Integer imageId) {
        PlaceImage image = placeImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));
        image.setLikes(image.getLikes() + 1);
        placeImageRepository.save(image);
    }

    @Override
    @Transactional
    public void addDislike(Integer userId, Integer imageId) {
        PlaceImage image = placeImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));
        image.setDislikes(image.getDislikes() + 1);
        placeImageRepository.save(image);
    }

    @Override
    @Transactional
    public void removeLike(Integer userId, Integer imageId) {
        PlaceImage image = placeImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));
        if (image.getLikes() > 0) {
            image.setLikes(image.getLikes() - 1);
            placeImageRepository.save(image);
        }
    }

    @Override
    @Transactional
    public void removeDislike(Integer userId, Integer imageId) {
        PlaceImage image = placeImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));
        if (image.getDislikes() > 0) {
            image.setDislikes(image.getDislikes() - 1);
            placeImageRepository.save(image);
        }
    }

    @Override
    @Transactional
    public PlaceImageResponse uploadPlaceImage(Integer placeId, MultipartFile imageFile, Integer userId) throws IOException {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found with id: " + placeId));

        User user = userId != null ?
                userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)) :
                null;

        String filename = generateImageFilename(imageFile.getOriginalFilename(), placeId);
        String path = StorageService.ROOT_PATH + "/places/" + placeId + "/images/" + filename;
        String imageUrl = storageService.uploadImage(imageFile, path);

        PlaceImage placeImage = new PlaceImage()
                .setPlace(place)
                .setImageUrl(imageUrl)
                .setUploadedBy(user)
                .setIsApproved(true)
                .setLikes(0)
                .setDislikes(0);

        PlaceImage savedImage = placeImageRepository.save(placeImage);
        log.info("Uploaded new image for place {} by user {}", placeId, userId);

        return placeImageMapper.toModel(savedImage);
    }

    @Override
    @Transactional
    public void rateImage(Integer imageId, boolean isLike) {
        PlaceImage image = placeImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));

        if (isLike) {
            image.setLikes(image.getLikes() + 1);
        } else {
            image.setDislikes(image.getDislikes() + 1);
        }

        placeImageRepository.save(image);
        log.info("Rated image {} with {}", imageId, isLike ? "like" : "dislike");
    }

    private String generateImageFilename(String originalFilename, Integer placeId) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "place_" + placeId + "_" + UUID.randomUUID() + extension;
    }
}