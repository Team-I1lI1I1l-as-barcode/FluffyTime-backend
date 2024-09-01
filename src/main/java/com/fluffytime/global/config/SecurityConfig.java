package com.fluffytime.global.config;

import static com.fluffytime.domain.user.entity.enums.RoleName.ROLE_ADMIN;

import com.fluffytime.global.auth.jwt.exception.CustomAuthenticationEntryPoint;
import com.fluffytime.global.auth.jwt.filter.JwtAuthenticationFilter;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.auth.oauth2.handler.CustomSuccessHandler;
import com.fluffytime.global.auth.oauth2.service.CustomOAuth2UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenizer jwtTokenizer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/oauth2/**",
                    "/login/oauth2/code/**",
                    "/login/**",
                    "/join/**",
                    "/api/users/**",
                    "/api/auth/**",
                    "/error",
                    "/static/**",
                    "/js/**",
                    "/css/**",
                    "/image/**",
                    "/favicon.ico"
                ).permitAll()
                .requestMatchers(
                    "/api/admin/**",
                    "/admin/**"
                ).hasRole(ROLE_ADMIN.getNoneHeaderName())
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer),
                UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling( exception ->
                exception
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedPage("/error.html")
            );

        http.oauth2Login((oauth2) -> oauth2
            .loginPage("/login")
            .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig
                .userService(customOAuth2UserService)
                ))
            .successHandler(customSuccessHandler)
        );

        http.sessionManagement(
            sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTION", "PUT"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
