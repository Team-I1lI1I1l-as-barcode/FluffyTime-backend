package com.fluffytime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/static/html/mypage/**", "/static/html/mypage/profiles/**",
                    "static/js/mypage/**", "static/js/mypage/profiles/**", "/static/css/mypage/**",
                    "/static/css/mypage/profiles/**",
                    "/login", "/",
                    "/mypage/{id}", " /mypage/profile/edit/{id}", "/api/mypage/info",
                    "/api/mypage/profiles/info")
                .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return http.build();


    }

}
