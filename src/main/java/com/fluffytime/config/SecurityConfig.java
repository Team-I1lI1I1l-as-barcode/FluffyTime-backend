package com.fluffytime.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 스프링 설정 클래스 어노테이션
@EnableWebSecurity // 웹 보안 활성화 어노테이션
@RequiredArgsConstructor // final 필드를 초기화하는 생성자 자동 생성
public class SecurityConfig {

    @Bean // 스프링 빈 등록 어노테이션
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()); // CSRF 보호 비활성화
        return http.build(); // SecurityFilterChain 객체 생성 및 반환
    }
}
