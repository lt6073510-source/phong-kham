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
public class MaNguon {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF để làm việc với API / AJAX Post dễ dàng hơn
            .csrf(csrf -> csrf.disable())
            
            // Nhường toàn bộ quyền kiểm tra đăng nhập cho Controller & HttpSession của bạn
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            
            // Tắt giao diện login mặc định và HTTP Basic của Spring Security
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // Cho phép hiển thị frame/iframe nếu ứng dụng có sử dụng
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}