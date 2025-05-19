package com.gather_club_back.gather_club_back.service;

import org.springframework.web.multipart.MultipartFile;

public interface YandexDiskService {
    String uploadImage(MultipartFile file, String path);
    void deleteImage(String filePath);
    String getPublicUrl(String filePath);
}
