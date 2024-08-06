package com.fluffytime.login.service;

import static com.fluffytime.login.dto.response.LoginResponseCode.LOGIN_SUCCESS;
import static com.fluffytime.login.dto.response.LoginResponseCode.REFRESH_TOKEN_GENERATE_SUCCESS;

import com.fluffytime.domain.RefreshToken;
import com.fluffytime.domain.User;
import com.fluffytime.login.dto.request.LoginUser;
import com.fluffytime.login.dto.response.ApiResponse;
import com.fluffytime.login.dto.response.UserLoginResponse;
import com.fluffytime.login.exception.MisMatchedPassword;
import com.fluffytime.login.exception.NotFoundToken;
import com.fluffytime.login.exception.NotFoundUser;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final HttpServletResponse httpServletResponse;

    public ApiResponse<UserLoginResponse> verifyUser(LoginUser user) {
        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow(NotFoundUser::new);

        if (bCryptPasswordEncoder.matches(findUser.getPassword(), user.getPassword())) {
            throw new MisMatchedPassword();
        }

        List<String> roles = findUser.getUserRoles().stream().map(role -> role
            .getRole()
            .getRoleName()
            .getName()
        ).toList();

        String accessToken = jwtTokenizer.createAccessToken(
            findUser.getUserId(),
            findUser.getEmail(),
            findUser.getNickname(),
            roles
        );

        String refreshToken = jwtTokenizer.createRefreshToken(
            findUser.getUserId(),
            findUser.getEmail(),
            findUser.getNickname(),
            roles
        );

        RefreshToken refreshTokenEntity = RefreshToken.builder()
            .value(refreshToken)
            .userId(findUser.getUserId())
            .build();

        refreshTokenService.addRefreshToken(refreshTokenEntity);

        UserLoginResponse response = UserLoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(findUser.getUserId())
            .email(findUser.getEmail())
            .build();

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT) / 1000); // 30분

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true); // HTTPS 사용시
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000)); // 7일

        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokenCookie);

        return ApiResponse.response(LOGIN_SUCCESS, response);
    }

    public ApiResponse<UserLoginResponse> getRefreshToken(HttpServletRequest req,
        HttpServletResponse res) {
        //할일!!
        //1. 쿠키로부터 리프레시토큰을 얻어온다.
        String refreshToken = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        //2-1. 없다.  (오류로  응담.
        if (refreshToken == null) {
            throw new NotFoundToken();
        }
        //2-2. 있을때.
        //3. 토큰으로부터 정보를얻어와요.
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf((Integer) claims.get("userId"));

        User user = userRepository.findById(userId)
            .orElseThrow(NotFoundUser::new);

        //4. accessToken 생성
        List roles = (List) claims.get("roles");

        String accessToken = jwtTokenizer.createAccessToken(userId, user.getEmail(),
            user.getNickname(), roles);

        //5. 쿠키 생성 response로 보내고.
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        res.addCookie(accessTokenCookie);

        // 6. 적절한 응답결과(ResponseEntity)를 생성해서 응답.
        UserLoginResponse response = UserLoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getUserId())
            .email(user.getEmail())
            .build();

        return ApiResponse.response(REFRESH_TOKEN_GENERATE_SUCCESS, response);
    }
}
