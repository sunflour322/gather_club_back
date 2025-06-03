package com.gather_club_back.gather_club_back.config;

import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@Slf4j
public class WebConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        log.info("Настроен StandardServletMultipartResolver для обработки загрузки файлов");
        return resolver;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Увеличиваем размеры для работы с изображениями
        DataSize maxFileSize = DataSize.ofMegabytes(20);
        DataSize maxRequestSize = DataSize.ofMegabytes(25);
        
        factory.setMaxFileSize(maxFileSize);
        factory.setMaxRequestSize(maxRequestSize);
        
        log.info("Настроен MultipartConfigElement: максимальный размер файла = {}, максимальный размер запроса = {}", 
                maxFileSize, maxRequestSize);
        
        return factory.createMultipartConfig();
    }
} 