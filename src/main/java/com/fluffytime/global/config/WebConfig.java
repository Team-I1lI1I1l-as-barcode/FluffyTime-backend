package com.fluffytime.global.config;

import com.fluffytime.domain.admin.interceptor.AdminCheckInterceptor;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.domain.user.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenizer jwtTokenizer;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor(jwtTokenizer))
            .order(1)
            .addPathPatterns(
                "/login",
                "/join/**"
            );

        registry.addInterceptor(new AdminCheckInterceptor(jwtTokenizer))
            .order(2)
            .addPathPatterns(
                "/api/auth/refreshToken"
            );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
    }
}
