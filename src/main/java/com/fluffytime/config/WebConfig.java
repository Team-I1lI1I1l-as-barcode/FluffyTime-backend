package com.fluffytime.config;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.user.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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
    }
}
