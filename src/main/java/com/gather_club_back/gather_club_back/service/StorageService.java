package com.gather_club_back.gather_club_back.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String ROOT_PATH = "GatherClub";
    String uploadImage(MultipartFile file, String path) throws IOException;
    void deleteImage(String filePath) throws IOException;
    String getPublicUrl(String filePath) throws IOException;
}
