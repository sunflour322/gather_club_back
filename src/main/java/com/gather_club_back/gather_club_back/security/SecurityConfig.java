package com.gather_club_back.gather_club_back.security;

import com.gather_club_back.gather_club_back.jwt.JwtAuthenticationFilter;
import com.gather_club_back.gather_club_back.jwt.JwtTokenProvider;
import com.gather_club_back.gather_club_back.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    final JwtTokenProvider tokenProvider;
    final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты
                        .requestMatchers(
                                "/auth/**",
                                "/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        // Защищённые эндпоинты
                        .requestMatchers("/places/**").authenticated()
                        .requestMatchers("/friendships/**").authenticated()
                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/user-places/**").authenticated()

                        // Разрешаем все остальные запросы (можно изменить на denyAll() если нужно)
                        .anyRequest().denyAll()
                ).addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Разрешаем все origins
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Все методы
        configuration.setAllowedHeaders(List.of("*")); // Все заголовки
        configuration.setExposedHeaders(List.of(
                "Authorization", "Content-Disposition")); // Какие заголовки доступны клиенту
        configuration.setAllowCredentials(false); // Отключаем credentials, так как используем "*" для origins


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Применяем ко всем путям

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}