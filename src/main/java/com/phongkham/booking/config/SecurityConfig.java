package com.phongkham.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Tắt CSRF để làm việc với API / AJAX Post
            .csrf(csrf -> csrf.disable())
            
            // 2. Mở toàn bộ quyền truy cập để Controller tự quản lý Session
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
            )
            
            // 3. Tắt HTTP Basic
            .httpBasic(basic -> basic.disable())
            
            // 4. Cho phép iFrame (nếu có dùng)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}