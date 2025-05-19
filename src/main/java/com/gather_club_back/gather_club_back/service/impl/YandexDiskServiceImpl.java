package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.service.YandexDiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class YandexDiskServiceImpl implements YandexDiskService {

    private final RestTemplate yandexDiskRestTemplate;

    @Value("${yandex.disk.api-url}")
    private String apiUrl;

    @Value("${yandex.disk.base-path}")
    private String basePath;

    @Override
    public String uploadImage(MultipartFile file, String path) {
        try {
            String fullPath = basePath + "/" + path;

            // 1. Получаем URL для загрузки
            String uploadUrl = getUploadUrl(fullPath);

            // 2. Загружаем файл
            uploadFile(uploadUrl, file);

            // 3. Публикуем файл
            return publishFile(fullPath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Yandex.Disk", e);
        }
    }

    private String getUploadUrl(String path) {
        String url = apiUrl + "/resources/upload?path=" + path + "&overwrite=true";
        ResponseEntity<String> response = yandexDiskRestTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get upload URL");
        }

        // Парсим JSON ответа для получения href (здесь упрощенно)
        return response.getBody(); // В реальности нужно парсить JSON
    }

    private void uploadFile(String uploadUrl, MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                uploadUrl, HttpMethod.PUT, requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Failed to upload file");
        }
    }

    private String publishFile(String path) {
        String url = apiUrl + "/resources/publish?path=" + path;

        // Используем exchange вместо put для получения ResponseEntity
        ResponseEntity<String> response = yandexDiskRestTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to publish file");
        }

        return getPublicUrl(path);
    }

    @Override
    public String getPublicUrl(String filePath) {
        String url = apiUrl + "/resources?path=" + filePath;
        ResponseEntity<String> response = yandexDiskRestTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get file info");
        }

        // Парсим JSON ответа для получения public_url (здесь упрощенно)
        return response.getBody(); // В реальности нужно парсить JSON
    }

    @Override
    public void deleteImage(String filePath) {
        String url = apiUrl + "/resources?path=" + filePath;
        yandexDiskRestTemplate.delete(url);
    }
}
