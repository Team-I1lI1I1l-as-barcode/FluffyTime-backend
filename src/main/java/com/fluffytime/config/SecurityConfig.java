package com.fluffytime.config;


import com.fluffytime.login.jwt.exception.CustomAuthenticationEntryPoint;
import com.fluffytime.login.jwt.filter.JwtAuthenticationFilter;
import com.fluffytime.login.jwt.util.JwtTokenizer;
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
    private final JwtTokenizer jwtTokenizer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/logout",
                    "/join/**",

                    "/static/**",
                    "/static/html/**",
                    "/html/**",
                    "/js/**",
                    "/api/users/**",
                    // 내가 추가한거
                    "/",
                    "/static/css/mypage/profiles/**",
                    "/static/html/mypage/profiles/**",
                    "/static/html/mypage/**",
                    "/static/js/mypage/**",
                    "/static/js/mypage/profiles/**",
                    "/mypage/{nickname}",
                    "/mypage/profile/edit/{nickname}",
                    "/api/mypage/info",
                    "/api/mypage/profiles/info",
                    "/api/mypage/profiles/edit",
                    "/api/mypage/profiles/check-username",
                    "/api/users/withdraw"
                ).permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer),
                UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(
                sessionManagement -> sessionManagement.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .httpBasic(httpBasic -> httpBasic.disable())
            .exceptionHandling(
                exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint));

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
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
