package com.fluffytime.login.jwt.exception;

import com.fluffytime.login.jwt.util.JwtResponseProvider;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
                setResponse(response, JwtErrorCode.INVALID_TOKEN);
            } else if (exception.equals("EXPIRED_TOKEN")) {
                // 예외 코드가 EXPIRED_TOKEN인 경우 : 만료된 JWT 토큰
                log.error("entry point >> expired token");
                setResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            } else if (exception.equals("UNSUPPORTED_TOKEN")) {
                // 예외 코드가 UNSUPPORTED_TOKEN인 경우 : 지원되지 않는 형식의 JWT 토큰
                log.error("entry point >> unsupported token");
                setResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals("NOT_FOUND_TOKEN")) {
                // 예외 코드가 NOT_FOUND_TOKEN인 경우 : JWT 토큰이 요청에 없음
                log.error("entry point >> not found token");
                setResponse(response, JwtErrorCode.NOT_FOUND_TOKEN);
            } else {
                // 위 조건에 해당하지 않는 경우 : 알 수 없는 에러
                setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
            }
        }

        log.info("여기가 실행됏네요");
        response.sendRedirect("/login");
        //페이지로 요청이 들어왔을 때 인증되지 않은 사용자라면 무조건 /login으로 리디렉션 시키겠다.

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
                setResponse(response, JwtErrorCode.INVALID_TOKEN);
            } else if (exception.equals("EXPIRED_TOKEN")) {
                // 예외 코드가 EXPIRED_TOKEN인 경우 : 만료된 JWT 토큰
                log.error("entry point >> expired token");
                setResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            } else if (exception.equals("UNSUPPORTED_TOKEN")) {
                // 예외 코드가 UNSUPPORTED_TOKEN인 경우 : 지원되지 않는 형식의 JWT 토큰
                log.error("entry point >> unsupported token");
                setResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals("NOT_FOUND_TOKEN")) {
                // 예외 코드가 NOT_FOUND_TOKEN인 경우 : JWT 토큰이 요청에 없음
                log.error("entry point >> not found token");
                setResponse(response, JwtErrorCode.NOT_FOUND_TOKEN);
            } else {
                // 위 조건에 해당하지 않는 경우 : 알 수 없는 에러
                setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
            }
        } else {
            // 예외가 없는 경우 : 알 수 없는 에러
            setResponse(response, JwtErrorCode.UNKNOWN_ERROR);
        }
    }

    // JWT 인증 과정에서 발생한 예외에 대한 HTTP 응답 설정
    // 응답의 Content-Type과 상태 코드를 설정하고, 예외 메시지와 코드를 포함한 JSON 응답을 생성하여 클라이언트에게 반환
    private void setResponse(HttpServletResponse response, JwtErrorCode exceptionCode)
        throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        // Gson 객체를 사용하여 HashMap을 JSON 문자열로 변환
        Gson gson = new Gson();
        String responseJson = gson.toJson(errorInfo);
        // 변환된 JSON 문자열을 응답으로 출력
        response.getWriter().print(responseJson);
    }
}

