package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.service.StorageService;
import com.google.cloud.storage.*;
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
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Override
    public String uploadImage(MultipartFile file, String path) throws IOException {
        String encodedPath = encodePath(path);

        BlobId blobId = BlobId.of(bucketName, encodedPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return generateDownloadUrl(encodedPath);
    }

    @Override
    public String getPublicUrl(String filePath) {
        String encodedPath = encodePath(filePath);
        return generateDownloadUrl(encodedPath);
    }

    @Override
    public void deleteImage(String filePath) throws IOException {
        String encodedPath = encodePath(filePath);
        BlobId blobId = BlobId.of(bucketName, encodedPath);
        boolean deleted = storage.delete(blobId);

        if (!deleted) {
            throw new IOException("Failed to delete file from Firebase Storage");
        }
    }

    private String generateDownloadUrl(String encodedPath) {
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName, encodedPath);
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
