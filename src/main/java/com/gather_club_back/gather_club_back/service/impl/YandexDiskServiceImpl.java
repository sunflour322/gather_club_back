package com.gather_club_back.gather_club_back.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gather_club_back.gather_club_back.service.YandexDiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class YandexDiskServiceImpl implements YandexDiskService {

    private final RestTemplate yandexDiskRestTemplate;
    private final ObjectMapper objectMapper;

    @Value("${yandex.disk.api-url}")
    private String apiUrl;

    @Value("${yandex.disk.base-path}")
    private String basePath;

    @Override
    public String uploadImage(MultipartFile file, String path) throws IOException {
        String fullPath = encodePath(basePath + "/" + path);

        // 1. Получаем URL для загрузки
        String uploadUrl = getUploadUrl(fullPath);

        // 2. Загружаем файл
        uploadFile(uploadUrl, file);

        // 3. Получаем публичную ссылку
        return getPublicUrl(fullPath);
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    private String getUploadUrl(String path) throws IOException {
        String url = apiUrl + "/resources/upload?path=" + path + "&overwrite=true";
        ResponseEntity<String> response = yandexDiskRestTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IOException("Failed to get upload URL");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("href").asText();
        } catch (Exception e) {
            throw new IOException("Failed to parse upload URL response", e);
        }
    }

    private void uploadFile(String uploadUrl, MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                uploadUrl, HttpMethod.PUT, requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new IOException("Failed to upload file to Yandex.Disk");
        }
    }

    private void publishFile(String path) throws IOException {
        String url = apiUrl + "/resources/publish?path=" + path;
        ResponseEntity<String> response = yandexDiskRestTemplate.exchange(
                url, HttpMethod.PUT, null, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Failed to publish file on Yandex.Disk");
        }
    }

    @Override
    public String getPublicUrl(String filePath) throws IOException {
        String path = encodePath(filePath);
        String url = apiUrl + "/resources/publish?path=" + path;

        // Публикуем файл
        ResponseEntity<String> publishResponse = yandexDiskRestTemplate.exchange(
                url, HttpMethod.PUT, null, String.class);

        if (publishResponse.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Failed to publish file");
        }

        // Получаем прямую ссылку на скачивание
        String downloadUrl = apiUrl + "/resources/download?path=" + path;
        ResponseEntity<String> downloadResponse = yandexDiskRestTemplate.getForEntity(downloadUrl, String.class);

        if (downloadResponse.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Failed to get download URL");
        }

        // Парсим ответ, чтобы получить прямую ссылку
        JsonNode root = objectMapper.readTree(downloadResponse.getBody());
        return root.path("href").asText();
    }

    private String getDownloadUrl(String path) throws IOException {
        String url = apiUrl + "/resources/download?path=" + path;
        ResponseEntity<String> response = yandexDiskRestTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IOException("Failed to get download URL");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("href").asText();
        } catch (Exception e) {
            throw new IOException("Failed to parse download URL response", e);
        }
    }

    @Override
    public void deleteImage(String filePath) throws IOException {
        String path = encodePath(filePath);
        String url = apiUrl + "/resources?path=" + path;

        try {
            yandexDiskRestTemplate.delete(url);
        } catch (Exception e) {
            throw new IOException("Failed to delete file from Yandex.Disk", e);
        }
    }
}