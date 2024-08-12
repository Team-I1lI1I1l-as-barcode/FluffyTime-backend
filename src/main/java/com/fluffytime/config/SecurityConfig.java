package com.fluffytime.config;

import com.fluffytime.login.jwt.dao.RefreshTokenDao;
import com.fluffytime.login.jwt.exception.CustomAuthenticationEntryPoint;
import com.fluffytime.login.jwt.filter.CustomLoginFilter;
import com.fluffytime.login.jwt.filter.CustomLogoutFilter;
import com.fluffytime.login.jwt.filter.JwtAuthenticationFilter;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenDao refreshTokenDao;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomLoginFilter loginFilter = new CustomLoginFilter(
            authenticationManager(authenticationConfiguration),
            jwtTokenizer,
            refreshTokenDao
        );
        loginFilter.setFilterProcessesUrl("/api/users/login");

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/logout",
                    "/join/**",
                    "/api/users/**",
                    "/error",
                    "/static/**",
                    "/html/**",
                    "/js/**",
                    "/css/**",
                    "/image/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(refreshTokenDao, jwtTokenizer),
                CustomLoginFilter.class)
            .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new CustomLogoutFilter(jwtTokenizer, refreshTokenDao),
                LogoutFilter.class)
            .formLogin(form -> form.disable())
            .sessionManagement(
                sessionManagement -> sessionManagement.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(
                exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint)
            )
        ;

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTION", "PUT"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
