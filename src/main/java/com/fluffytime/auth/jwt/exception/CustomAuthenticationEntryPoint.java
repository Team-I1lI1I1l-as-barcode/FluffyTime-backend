package com.fluffytime.auth.jwt.exception;

import com.fluffytime.auth.jwt.util.JwtResponseProvider;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// 시큐리티가 인증되지 않은 사용자가 인증이 필요한 리소스에 접근 할때 동작하게 하는 인터페이스
// 사용자가 로그인하지 않은 상태에서 보호된 페이지에 접근하려고 할 때
// 사용자의 세션이 만료되어 인증이 필요한 자원에 접근하지 못할 때
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //사용자가 인증되지 않았을때.. 어떻게 처리할지를 구현함.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        // 요청 속성에서 예외 메시지 가져오기
        String exception = (String) request.getAttribute("exception");

        //RESTful로 요청한건지..  그냥 페이지 요청한건지 구분해서 다르게 동작하도록 구현.
        if (isRestRequest(request)) { // RESTful 요청이라면
            handleRestResponse(request, response, exception); // 관련 핸들러 실행
        } else { // 페이지 요청이라면
            handlePageResponse(request, response, exception); // 관련 핸들러 실행
        }
    }

    // 사용자 요청이 RESTful인지 판별
    private boolean isRestRequest(HttpServletRequest request) {
        // 요청 헤더에서 'X-Requested-With' 헤더 값을 가져옴
        String requestedWithHeader = request.getHeader("X-Requested-With");

        // 'X-Requested-With' 헤더 값이 'XMLHttpRequest'인지 확인
        // 또는 요청 URI가 '/api/'로 시작하는지 확인하여
        // 둘 중 하나라도 참이면 RESTful 요청으로 간주하고 true 반환
        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI()
            .startsWith("/api/");
    }

    // 사용자 요청이 페이지 요청라면 이에 대한 처리 구현
    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response,
        String exception) throws IOException {
        // 로그에 에러 메시지 기록
        log.error("Page Request - Commence Get Exception : {}", exception);

        // 에러 존재할 시
        // 에러가 존재할 경우 종류에 따라서 응답 설정
        if (exception != null) {
            if (exception.equals("INVALID_TOKEN")) {
                // 예외 코드가 INVALID_TOKEN인 경우: 유효하지 않은 JWT 토큰
                log.error("entry point >> invalid token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.INVALID_TOKEN);
            } else if (exception.equals("EXPIRED_TOKEN")) {
                // 예외 코드가 EXPIRED_TOKEN인 경우 : 만료된 JWT 토큰
                log.error("entry point >> expired token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            } else if (exception.equals("UNSUPPORTED_TOKEN")) {
                // 예외 코드가 UNSUPPORTED_TOKEN인 경우 : 지원되지 않는 형식의 JWT 토큰
                log.error("entry point >> unsupported token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals("NOT_FOUND_TOKEN")) {
                // 예외 코드가 NOT_FOUND_TOKEN인 경우 : JWT 토큰이 요청에 없음
                log.error("entry point >> not found token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.TOKEN_NOT_FOUND);
            } else {
                // 위 조건에 해당하지 않는 경우 : 알 수 없는 에러
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
            }
        }
        String requestURI = request.getRequestURI();
        log.info("redirect url = {}", request.getRequestURI());
        response.sendRedirect("/login?redirectURL="+requestURI);
    }

    // 사용자 요청이 RESTful라면 이에 대한 처리 구현
    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response,
        String exception) throws IOException {
        log.error("Rest Request - Commence Get Exception : {}", exception);

        // 에러가 존재할 경우 종류에 따라서 응답 설정
        if (exception != null) {
            if (exception.equals("INVALID_TOKEN")) {
                // 예외 코드가 INVALID_TOKEN인 경우: 유효하지 않은 JWT 토큰
                log.error("entry point >> invalid token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.INVALID_TOKEN);
            } else if (exception.equals("EXPIRED_TOKEN")) {
                // 예외 코드가 EXPIRED_TOKEN인 경우 : 만료된 JWT 토큰
                log.error("entry point >> expired token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            } else if (exception.equals("UNSUPPORTED_TOKEN")) {
                // 예외 코드가 UNSUPPORTED_TOKEN인 경우 : 지원되지 않는 형식의 JWT 토큰
                log.error("entry point >> unsupported token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals("NOT_FOUND_TOKEN")) {
                // 예외 코드가 NOT_FOUND_TOKEN인 경우 : JWT 토큰이 요청에 없음
                log.error("entry point >> not found token");
                JwtResponseProvider.setResponse(response, JwtErrorCode.TOKEN_NOT_FOUND);
            } else {
                // 위 조건에 해당하지 않는 경우 : 알 수 없는 에러
                JwtResponseProvider.setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
            }
        } else {
            // 예외가 없는 경우 : 알 수 없는 에러
            JwtResponseProvider.setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
        }
    }
}

