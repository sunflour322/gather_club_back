package com.gather_club_back.gather_club_back.yandex_disk;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfiguration {

    @PostConstruct
    public void init() {
        try {
            log.info("Loading Firebase credentials...");
            ClassPathResource resource = new ClassPathResource("firebase-config.json");

            if (!resource.exists()) {
                throw new FileNotFoundException("Firebase config file not found in classpath");
            }

            InputStream serviceAccount = resource.getInputStream();

            log.info("Initializing Firebase App...");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("flutter-films-mukachev.appspot.com")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase App initialized.");
            } else {
                log.info("Firebase App already initialized.");
            }

        } catch (Exception e) {
            log.error("🔥 Failed to initialize Firebase", e);
            throw new RuntimeException(e);
        }
    }
}



