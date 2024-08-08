package com.fluffytime.login.service;

import static com.fluffytime.login.dto.response.LoginResponseCode.LOGIN_SUCCESS;
import static com.fluffytime.login.dto.response.LoginResponseCode.REFRESH_TOKEN_GENERATE_SUCCESS;

import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.RefreshToken;
import com.fluffytime.domain.User;
import com.fluffytime.login.dto.request.LoginUser;
import com.fluffytime.login.dto.response.ApiResponse;
import com.fluffytime.login.dto.response.UserLoginResponse;
import com.fluffytime.login.exception.jwt.NotFoundToken;
import com.fluffytime.login.exception.login.MisMatchedPassword;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final HttpServletResponse httpServletResponse;

    public UserLoginResponse verifyUser(LoginUser loginUser,
        HttpServletResponse response) {
        User user = userRepository.findByEmail(loginUser.getEmail()).orElseThrow(NotFoundUser::new);
        log.info("password = {}", loginUser.getPassword());

        // !!! 파라미터순서 중요 !!!
        if (!bCryptPasswordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new MisMatchedPassword();
        }

        // 3. 유저의 권한 꺼내오기
        List<String> roles = user.getUserRoles().stream().map(role -> role
            .getRole()
            .getRoleName()
            .getName()
        ).toList();

        // accessToken 발급하기
        String accessToken = jwtTokenizer.createAccessToken(
            user.getUserId(),
            user.getEmail(),
            user.getNickname(),
            roles
        );

        // refresh 발급하기
        String refreshToken = jwtTokenizer.createRefreshToken(
            user.getUserId(),
            user.getEmail(),
            user.getNickname(),
            roles
        );

        // refresh 토큰 db에 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
            .value(refreshToken)
            .userId(user.getUserId())
            .build();
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        // 응답으로 보낼 정보 설정 (토큰 + 유저 정보)

        // 쿠키에 토큰 저장
        // 엑세스 토큰 쿠키
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT) / 1000); // 30분

        // 리프레시 토큰 쿠키
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000)); // 7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();

    }

    // 리프레시 토큰 -> acc 토큰 생성
    public UserLoginResponse getRefreshToken(HttpServletRequest req,
        HttpServletResponse res) {

        //1. 쿠키 안에 있는 refresh token 꺼내기
        Cookie[] cookies = req.getCookies();
        String refreshToken = null;

        if (cookies != null) { // 쿠키들이 존재한다면
            for (Cookie cookie : cookies) {
                // 쿠키의 키 이름이 refreshToken 인 것 찾기
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        // 만약 쿠키가 없거나, 쿠키들을 살펴봤는데 refreshToken이 없다면
        if (refreshToken == null) {
            // 에러 메세지 반환
            throw new NotFoundToken();
        }

        //2. 쿠키에 refreshToken이 있다면, 정보 얻어오기
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        // 클레임 객체를 통해 주키 id 찾기
        Long userId = Long.valueOf((Integer) claims.get("userId"));
        // id를 이용하여 user 사용자 객체 찾기 -> 없을 시 예외 발생
        User user = userRepository.findById(userId)
            .orElseThrow(NotFoundUser::new);
        // 클래임 객체를 통해 해당 사용자 권한들 뽑기
        List roles = (List) claims.get("roles");

        //3. accessToken 생성
        String accessToken = jwtTokenizer.createAccessToken(userId, user.getEmail(),
            user.getNickname(), roles);

        //4. 쿠키 생성 후 accessToken 담아서 응답으로 보낸다.
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        res.addCookie(accessTokenCookie);

        // 6. 적절한 응답결과(ResponseEntity)를 생성해서 응답.
        return UserLoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getUserId())
            .email(user.getEmail())
            .build();
    }
}
