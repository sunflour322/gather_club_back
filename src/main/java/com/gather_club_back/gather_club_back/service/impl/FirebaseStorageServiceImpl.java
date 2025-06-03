package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.service.StorageService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseStorageServiceImpl implements StorageService {

    private final String bucketName = "flutter-films-mukachev.appspot.com";
    private final String ROOT_PATH = "GatherClub";

    @Override
    public String uploadImage(MultipartFile file, String path) throws IOException {
        log.info("Начало загрузки изображения по пути: {}", path);
        
        try {
            // Нормализуем путь - убедимся, что он не содержит двойного кодирования
            String normalizedPath = normalizePath(path);
            log.info("Нормализованный путь: {}", normalizedPath);
            
            // Получаем экземпляр Storage через StorageClient
            com.google.cloud.storage.Storage storage = StorageClient.getInstance().bucket().getStorage();
            
            String encodedPath = encodePath(normalizedPath);
            log.info("Закодированный путь: {}", encodedPath);

            BlobId blobId = BlobId.of(bucketName, normalizedPath); // Используем нормализованный путь без кодирования
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

            log.info("Создание блоба с типом содержимого: {}", file.getContentType());
        storage.create(blobInfo, file.getBytes());
            log.info("Файл успешно загружен в Firebase Storage");
            
            String downloadUrl = generateDownloadUrl(encodedPath);
            log.info("Сгенерирован URL для скачивания: {}", downloadUrl);
            
            return downloadUrl;
        } catch (Exception e) {
            log.error("Ошибка при загрузке изображения: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String getPublicUrl(String filePath) {
        String encodedPath = encodePath(normalizePath(filePath));
        return generateDownloadUrl(encodedPath);
    }

    @Override
    public void deleteImage(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            log.warn("Попытка удалить пустой путь к файлу");
            return;
        }
        
        try {
            // Извлекаем путь из URL
            String path = extractPathFromUrl(filePath);
            if (path == null) {
                log.warn("Не удалось извлечь путь из URL: {}", filePath);
                return;
            }
            
            // Нормализуем путь перед использованием
            String normalizedPath = normalizePath(path);
            log.info("Попытка удалить файл по пути: {}", normalizedPath);
            
            com.google.cloud.storage.Storage storage = StorageClient.getInstance().bucket().getStorage();
            BlobId blobId = BlobId.of(bucketName, normalizedPath); // Используем нормализованный путь без кодирования
        boolean deleted = storage.delete(blobId);

            if (deleted) {
                log.info("Файл успешно удален: {}", normalizedPath);
            } else {
                log.warn("Файл не был найден или не удалось удалить: {}", normalizedPath);
            }
        } catch (Exception e) {
            log.warn("Ошибка при попытке удалить файл {}: {}", filePath, e.getMessage());
            // Не выбрасываем исключение, чтобы процесс обновления аватара мог продолжиться
        }
    }

    private String generateDownloadUrl(String encodedPath) {
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName, encodedPath);
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
    
    /**
     * Метод для извлечения пути файла из полного URL Firebase Storage
     */
    private String extractPathFromUrl(String url) {
        try {
            // Предполагаем URL формата: https://firebasestorage.googleapis.com/v0/b/BUCKET/o/PATH?alt=media&token=TOKEN
            if (url == null || !url.contains("/o/")) {
                return null;
            }
            
            String encodedPath = url.substring(url.indexOf("/o/") + 3);
            if (encodedPath.contains("?")) {
                encodedPath = encodedPath.substring(0, encodedPath.indexOf("?"));
            }
            
            // URL-декодирование пути
            return java.net.URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("Ошибка при извлечении пути из URL: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Метод для нормализации пути, убирает двойное кодирование и исправляет слэши
     */
    private String normalizePath(String path) {
        if (path == null) return null;
        
        // Убираем двойное кодирование, если оно есть (например, %252F вместо %2F)
        String normalized = path;
        while (normalized.contains("%25")) {
            try {
                normalized = java.net.URLDecoder.decode(normalized, StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                break; // Если возникла ошибка декодирования, прерываем цикл
            }
        }
        
        // Убедимся, что путь начинается с правильного префикса
        if (!normalized.startsWith(ROOT_PATH) && !normalized.startsWith("/" + ROOT_PATH)) {
            if (normalized.startsWith("/")) {
                normalized = ROOT_PATH + normalized;
            } else {
                normalized = ROOT_PATH + "/" + normalized;
            }
        }
        
        // Исправляем двойные слэши, если они есть
        while (normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        
        return normalized;
    }
}
